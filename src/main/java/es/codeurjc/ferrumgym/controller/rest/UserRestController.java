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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
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

    @Operation(summary = "Get all users paginated")
    @GetMapping
    public ResponseEntity<Page<UserResponseDTO>> getUsers(@PageableDefault(size = 10) Pageable pageable) {
        // Paginated list that converts Entities to UserResponseDTOs to hide sensitive data
        Page<User> users = userService.findAll(pageable);
        // Service return entities, controller mapper to DTO
        return ResponseEntity.ok(users.map(userMapper::toDTO));
    }

    // ... (Other endpoints remain unchanged)

    @Operation(summary = "Update user profile")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User updated"),
        @ApiResponse(responseCode = "403", description = "Forbidden: Not the owner or admin"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(@PathVariable Long id, @RequestBody UserResponseDTO userDto) {
        // 1. We use the mapper to convert the input Record to an Entity
        User userDetails = userMapper.toEntity(userDto);
        
        // 2. The service now receives and returns a pure Entity
        User updatedUser = userService.update(id, userDetails);
        
        // 3. We map the resulting entity back to the Response Record
        return ResponseEntity.ok(userMapper.toDTO(updatedUser));
    }

    @Operation(summary = "Delete a user by id")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") // Strict enforcement: Only the admin should be able to fully delete users
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        // The service layer handles throwing a 404 Exception if the user does not exist
        userService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get the user's profile image")
    @GetMapping("/{id}/image")
    public ResponseEntity<byte[]> getUserImage(@PathVariable Long id) {
        return userService.findById(id)
                .filter(user -> user.getImage() != null)
                .map(user -> ResponseEntity.ok()
                        .header("Content-Type", "image/jpeg") // Adjust MIME type if necessary
                        .body(user.getImage()))
                .orElse(ResponseEntity.notFound().build());
    }
}