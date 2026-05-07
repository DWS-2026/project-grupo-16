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

    // --- MÉTODOS DE PERSISTENCIA Y LÓGICA ---

    // Guarda el usuario y cifra la contraseña si es necesario
    public void save(User user) {
        if (user.getPassword() != null && !user.getPassword().startsWith("$2a$")) {
            String passwordCifrada = passwordEncoder.encode(user.getPassword());
            user.setPassword(passwordCifrada);
        }
        userRepository.save(user);
    }

    public User update(Long id, User updatedUserData) {
        // 1. Buscamos al usuario original en la DB
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        // 2. Control de seguridad
        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        // IMPORTANTE: Usamos ROLE_ADMIN y comparamos el email del dueño
        boolean isOwner = user.getEmail().equals(currentEmail);
        boolean isAdmin = currentUser.getRoles().contains("ROLE_ADMIN");

        if (isOwner || isAdmin) {
            user.setName(updatedUserData.getName());

            // B. Actualizamos el Email (¡Esta línea te faltaba!)
            user.setEmail(updatedUserData.getEmail());


            // C. Actualizamos la Contraseña (¡Solo si el usuario envía una nueva!)
            // Asumo que en tu entidad el campo se llama encodedPassword
            if (updatedUserData.getEncodedPassword() != null && !updatedUserData.getEncodedPassword().isEmpty()) {
                String newHash = passwordEncoder.encode(updatedUserData.getEncodedPassword());
                user.setEncodedPassword(newHash);
            }

            // D. Solo el Admin puede cambiar Roles
            if (isAdmin && updatedUserData.getRoles() != null) {
                user.setRoles(updatedUserData.getRoles());
            }

            // 3. Guardamos los cambios
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