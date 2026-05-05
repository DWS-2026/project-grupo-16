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

    // --- MÉTODOS DE BÚSQUEDA (Usan Entidades) ---
    
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

    //Guarda el usuario y cifra la contraseña si es necesario
    public void save(User user) {
        if (user.getPassword() != null && !user.getPassword().startsWith("$2a$")) {
            String passwordCifrada = passwordEncoder.encode(user.getPassword());
            user.setPassword(passwordCifrada);
        }
        userRepository.save(user);
    }

    //Actualiza los datos del usuario con control de permisos (Dueño o Admin)
    //Recibe y devuelve la entidad pura
    public User update(Long id, User updatedUserData) {
        // 1. Buscamos al usuario original
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        // 2. Control de seguridad (Punto 11 de la rúbrica)[cite: 10]
        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        if (user.getEmail().equals(currentEmail) || currentUser.getRoles().contains("ADMIN")) {
            // Actualizamos solo los campos permitidos
            user.setName(updatedUserData.getName());
            
            // Si el admin cambia roles o imagen, se gestionaría aquí
            if (updatedUserData.getRoles() != null) {
                user.setRoles(updatedUserData.getRoles());
            }

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