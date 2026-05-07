package es.codeurjc.ferrumgym.dto;

import es.codeurjc.ferrumgym.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 * Mapper interface for User entity and UserResponseDTO.
 * MapStruct automatically implements this interface at compile time.
 */
@Mapper(componentModel = "spring")
public interface UserMapper {

    // --- From Entity to DTO (Output) ---
    @Mapping(target = "imageUrl", expression = "java(generateImageUrl(user))")
    // Optional: explicitly ignore the password in the output for security reasons
    @Mapping(target = "password", ignore = true) 
    UserResponseDTO toDTO(User user);

    // --- From DTO to Entity (Input) ---
    // THIS IS THE KEY LINE: 
    // Maps the 'password' field from the Record to the 'encodedPassword' field of the User Entity
    @Mapping(target = "encodedPassword", source = "password")
    User toEntity(UserResponseDTO dto);

    // Custom logic to generate the full access URL for the user's avatar image
    default String generateImageUrl(User user) {
        if (user == null || user.getId() == null || user.getImage() == null) { return null;}
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/v1/users/")
                .path(user.getId().toString())
                .path("/image")
                .toUriString();
    }
}