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

    @Operation(summary = "Get a list of all users paginated")
    @GetMapping
    public ResponseEntity<Page<UserResponseDTO>> getUsers(@PageableDefault(size = 10) Pageable pageable) {
        // Paginated list that converts Entities to UserResponseDTOs to hide sensitive data
        Page<User> users = userService.findAll(pageable);
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
        // 1. Extract data
        String name = request.get("name");
        String email = request.get("email");
        String password = request.get("password");
        // 2. Validation
        if (name == null || name.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Name is required");
        }
        if (email == null || !email.contains("@")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A valid email is required");
        }
        if (password == null || password.length() < 6) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password must be at least 6 characters");
        }
        // 3. Saving logic
        User newUser = new User(name, email, password, java.util.List.of("ROLE_USER"));
        userService.save(newUser);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(newUser.getId())
                .toUri();

        return ResponseEntity.created(location).body(userMapper.toDTO(newUser));
    }

    // --- NEW: IMAGE UPLOAD/EDIT METHOD ---
    @Operation(summary = "Upload or update a profile image for a user")
    @PutMapping("/{id}/image") 
    public ResponseEntity<Void> updateUserImage(@PathVariable Long id, @RequestParam MultipartFile imageFile) throws java.io.IOException {
        
        // 1. We check if the user exists
        User user = userService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        
        // 2. We extract the bytes from the image and store them in the entity
        if (!imageFile.isEmpty()) {
            user.setImage(imageFile.getBytes());
            userService.save(user); // We saved the user with their new photo
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The image file is empty");
        }
        
        // 3. We return 204 (No Content) because everything went well
        return ResponseEntity.noContent().build();
    }
    
    @Operation(summary = "Update an existing user profile")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User updated"),
        @ApiResponse(responseCode = "403", description = "Forbidden: Not the owner or admin"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(@PathVariable Long id, @RequestBody UserResponseDTO userDto) {
        // 1. We use the mapper to convert the input DTO Record into a domain Entity
        User userDetails = userMapper.toEntity(userDto);
        
        // 2. The service receives the entity and enforces IDOR security logic
        User updatedUser = userService.update(id, userDetails);
        
        // 3. We map the resulting entity back to the Response DTO for safe client consumption
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