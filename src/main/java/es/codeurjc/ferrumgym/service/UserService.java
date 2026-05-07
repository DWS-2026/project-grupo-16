package es.codeurjc.ferrumgym.service;

import es.codeurjc.ferrumgym.model.User;
import es.codeurjc.ferrumgym.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public Page<User> findAll(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // --- PERSISTENCE AND BUSINESS LOGIC METHODS ---

    // Saves the user and encrypts the password if necessary
    public void save(User user) {
        if (user.getPassword() != null && !user.getPassword().startsWith("$2a$")) {
            String passwordCifrada = passwordEncoder.encode(user.getPassword());
            user.setPassword(passwordCifrada);
        }
        userRepository.save(user);
    }

    public User update(Long id, User updatedUserData) {
        // 1. Fetch the original user from the DB
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        // 2. Security access control
        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        // IMPORTANT: Use ROLE_ADMIN and compare the owner's email for authorization
        boolean isOwner = user.getEmail().equals(currentEmail);
        boolean isAdmin = currentUser.getRoles().contains("ROLE_ADMIN");

        if (isOwner || isAdmin) {
            user.setName(updatedUserData.getName());

            // B. Update the Email (Critical missing line added for completeness)
            user.setEmail(updatedUserData.getEmail());

            // C. Update the Password (Only if the user sends a new one!)
            // Assuming the entity field is named encodedPassword
            if (updatedUserData.getEncodedPassword() != null && !updatedUserData.getEncodedPassword().isEmpty()) {
                String newHash = passwordEncoder.encode(updatedUserData.getEncodedPassword());
                user.setEncodedPassword(newHash);
            }

            // D. Only the Admin can change Roles
            if (isAdmin && updatedUserData.getRoles() != null) {
                user.setRoles(updatedUserData.getRoles());
            }

            // 3. Save the applied changes
            return userRepository.save(user);

        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permiso para editar este perfil");
        }
    }

    public void deleteById(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        userRepository.deleteById(id);
    }
}