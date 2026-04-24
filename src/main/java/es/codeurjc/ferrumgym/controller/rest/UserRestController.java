package es.codeurjc.ferrumgym.controller.rest;

import es.codeurjc.ferrumgym.dto.UserResponseDTO;
import es.codeurjc.ferrumgym.model.User;
import es.codeurjc.ferrumgym.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/users")
public class UserRestController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // 3. Paginated endpoint to list all users
    @GetMapping("/")
    public ResponseEntity<Page<UserResponseDTO>> getUsers(@PageableDefault(size = 10) Pageable pageable) {
        Page<User> users = userService.findAll(pageable);

        // Map Entity to DTO to hide sensitive data like passwords (Rubric point 22)
        Page<UserResponseDTO> response = users.map(UserResponseDTO::new);

        return ResponseEntity.ok(response);
    }

    // 4. Endpoint to get details of a specific user
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        Optional<User> user = userService.findById(id);

        if (user.isPresent()) {
            return ResponseEntity.ok(new UserResponseDTO(user.get()));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // 2. & 5. Signup / User creation endpoint
    @PostMapping("/")
    public ResponseEntity<UserResponseDTO> createUser(@RequestBody User user) {

        // Encode password before saving to the database
        user.setEncodedPassword(passwordEncoder.encode(user.getEncodedPassword()));

        // Set default role for new signups
        user.setRoles(List.of("USER"));

        userService.save(user);

        // 19. Return 'Location' header with the URI of the newly created resource
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(user.getId())
                .toUri();

        return ResponseEntity.created(location).body(new UserResponseDTO(user));
    }

    // 7. Endpoint to delete a user
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        Optional<User> user = userService.findById(id);

        if (user.isPresent()) {
            userService.deleteById(id);
            return ResponseEntity.noContent().build(); // HTTP 204 No Content
        } else {
            return ResponseEntity.notFound().build();  // HTTP 404 Not Found
        }
    }

    // 9. Endpoint to visualize user images
    @GetMapping("/{id}/image")
    public ResponseEntity<byte[]> getUserImage(@PathVariable Long id) {
        Optional<User> user = userService.findById(id);

        if (user.isPresent() && user.get().getImage() != null) {
            return ResponseEntity.ok()
                    .header("Content-Type", "image/jpeg")
                    .body(user.get().getImage());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
