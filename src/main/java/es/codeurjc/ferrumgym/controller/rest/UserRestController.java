package es.codeurjc.ferrumgym.controller.rest;

import es.codeurjc.ferrumgym.dto.UserResponseDTO;
import es.codeurjc.ferrumgym.model.User;
import es.codeurjc.ferrumgym.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/users")
public class UserRestController {

    @Autowired
    private UserService userService;

    // 3. Listado paginado (Usa tu UserResponseDTO)
    @GetMapping
    public ResponseEntity<Page<UserResponseDTO>> getUsers(@PageableDefault(size = 10) Pageable pageable) {
        Page<User> users = userService.findAll(pageable);
        return ResponseEntity.ok(users.map(UserResponseDTO::new));
    }

    // 4. Detalle de usuario
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        return userService.findById(id)
                .map(user -> ResponseEntity.ok(new UserResponseDTO(user)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(@RequestBody java.util.Map<String, String> request) {
    
        // Extraemos los datos del mapa (el JSON que llega de Postman)
        String name = request.get("name");
        String email = request.get("email");
        String password = request.get("password");

        User newUser = new User(name, email, password, java.util.List.of("ROLE_USER"));
        userService.save(newUser); 

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(newUser.getId())
                .toUri();
        return ResponseEntity.created(location).body(new UserResponseDTO(newUser));
    }

    // NUEVO: Endpoint para editar perfil (Conecta con la lógica IDOR del Service)
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(@PathVariable Long id, @RequestBody UserResponseDTO userDto) {
        UserResponseDTO updated = userService.update(id, userDto);
        return ResponseEntity.ok(updated);
    }

    // 7. Borrado de usuario
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") // Solo el admin debería poder borrar usuarios completos
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteById(id); // El service ya lanza 404 si no existe
        return ResponseEntity.noContent().build();
    }

    // 9. Imágenes de usuario
    @GetMapping("/{id}/image")
    public ResponseEntity<byte[]> getUserImage(@PathVariable Long id) {
        Optional<User> user = userService.findById(id);

        if (user.isPresent() && user.get().getImage() != null) {
            return ResponseEntity.ok()
                    .header("Content-Type", "image/jpeg") // O el tipo que uses
                    .body(user.get().getImage());
        }
        return ResponseEntity.notFound().build();
    }
}