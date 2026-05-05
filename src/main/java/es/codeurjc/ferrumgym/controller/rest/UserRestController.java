package es.codeurjc.ferrumgym.controller.rest;

import es.codeurjc.ferrumgym.dto.UserMapper;
import es.codeurjc.ferrumgym.dto.UserResponseDTO;
import es.codeurjc.ferrumgym.model.User;
import es.codeurjc.ferrumgym.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")
public class UserRestController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper userMapper;

    @Operation(summary = "Get a list of all users paginated")
    @GetMapping
    public ResponseEntity<Page<UserResponseDTO>> getUsers(@PageableDefault(size = 10) Pageable pageable) {
        Page<User> users = userService.findAll(pageable);
        // El servicio devuelve entidades, el controlador mapea a DTO
        return ResponseEntity.ok(users.map(userMapper::toDTO));
    }

    @Operation(summary = "Get a user by its id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Found the user",
            content = { @Content(mediaType = "application/json",
            schema = @Schema(implementation = UserResponseDTO.class)) }),
        @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        return userService.findById(id)
                .map(user -> ResponseEntity.ok(userMapper.toDTO(user)))
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Create a new user")
    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(@RequestBody Map<String, String> request) {
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
        
        return ResponseEntity.created(location).body(userMapper.toDTO(newUser));
    }

    @Operation(summary = "Update an existing user profile")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User updated successfully"),
        @ApiResponse(responseCode = "403", description = "Forbidden: Not the owner or admin"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(@PathVariable Long id, @RequestBody UserResponseDTO userDto) {
        // 1. Usamos el mapper para convertir el Record de entrada a Entidad
        User userDetails = userMapper.toEntity(userDto);
        
        // 2. El servicio ahora recibe y devuelve una Entidad pura
        User updatedUser = userService.update(id, userDetails);
        
        // 3. Mapeamos la entidad resultante de vuelta al Record de respuesta
        return ResponseEntity.ok(userMapper.toDTO(updatedUser));
    }

    @Operation(summary = "Delete a user by id")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get the user's profile image")
    @GetMapping("/{id}/image")
    public ResponseEntity<byte[]> getUserImage(@PathVariable Long id) {
        return userService.findById(id)
                .filter(user -> user.getImage() != null)
                .map(user -> ResponseEntity.ok()
                        .header("Content-Type", "image/jpeg")
                        .body(user.getImage()))
                .orElse(ResponseEntity.notFound().build());
    }
}