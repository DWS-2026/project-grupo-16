package es.codeurjc.ferrumgym.dto;

import es.codeurjc.ferrumgym.model.Review;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Collection;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ReviewMapper {

    // --- De Entidad a DTO (Salida) ---
    @Mapping(target = "userName", source = "user.name")
    @Mapping(target = "activityName", source = "activity.name")
    @Mapping(target = "imageUrl", expression = "java(generateImageUrl(review))")
    ReviewDTO toDTO(Review review);

    // --- De DTO a Entidad (Entrada) ---
    // MapStruct generará el código para convertir el DTO de vuelta a la Entidad
    Review toEntity(ReviewDTO dto);

    List<ReviewDTO> toDTOs(Collection<Review> reviews);

    //Genera la URL para acceder a la imagen de la reseña
    default String generateImageUrl(Review review) {
        if (review.getImageFile() == null) return null; 
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/v1/reviews/")
                .path(review.getId().toString())
                .path("/image")
                .toUriString();
    }
}