package es.codeurjc.ferrumgym.service;

import es.codeurjc.ferrumgym.dto.UserResponseDTO;
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
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // --- MÉTODOS PARA LA WEB (Usan Entidades) ---
    
    public List<User> findAll() {
        return userRepository.findAll();
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public void save(User user) {
    // Solo ciframos si la contraseña no está ya cifrada (los hashes de BCrypt empiezan por $2a$)
    if (user.getPassword() != null && !user.getPassword().startsWith("$2a$")) {
        String passwordCifrada = passwordEncoder.encode(user.getPassword());
        user.setPassword(passwordCifrada);
    }
    userRepository.save(user);
}

    // --- MÉTODOS PARA LA API (Usan DTOs) ---

    public List<UserResponseDTO> findAllDTOs() {
        return userRepository.findAll().stream()
                .map(UserResponseDTO::new)
                .collect(Collectors.toList());
    }

    public UserResponseDTO update(Long id, UserResponseDTO userDto) {
        // 1. Buscamos al usuario que se quiere editar
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        // 2. CONTROL DE DUEÑO (Punto 11): ¿Quién está logueado?
        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        // Solo puede editar si es su propio perfil O es ADMIN
        if (user.getEmail().equals(currentEmail) || currentUser.getRoles().contains("ADMIN")) {
            user.setName(userDto.getName());
            // No actualizamos email ni password aquí para evitar problemas de seguridad críticos
            User saved = userRepository.save(user);
            return new UserResponseDTO(saved);
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

    public Page<User> findAll(Pageable pageable) {
        return userRepository.findAll(pageable);
    }
}