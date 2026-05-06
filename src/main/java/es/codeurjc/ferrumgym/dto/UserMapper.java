package es.codeurjc.ferrumgym.dto;

import es.codeurjc.ferrumgym.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Mapper(componentModel = "spring")
public interface UserMapper {

    // --- De Entidad a DTO (Salida) ---
    @Mapping(target = "imageUrl", expression = "java(generateImageUrl(user))")
    UserResponseDTO toDTO(User user);

    // --- De DTO a Entidad (Entrada) ---
    // AÑADE ESTA LÍNEA: MapStruct se encarga de crear el objeto User automáticamente
    User toEntity(UserResponseDTO dto);

    // Lógica para generar la URL de la imagen
    default String generateImageUrl(User user) {
        if (user == null || user.getId() == null || user.getImage() == null) { return null;}
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/v1/users/")
                .path(user.getId().toString())
                .path("/image")
                .toUriString();
    }
}