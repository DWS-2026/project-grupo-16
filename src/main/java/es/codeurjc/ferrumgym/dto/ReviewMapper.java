package es.codeurjc.ferrumgym.dto;

import es.codeurjc.ferrumgym.model.Review;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Collection;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ReviewMapper {

    // --- From Entity to DTO (Output) ---
    @Mapping(target = "userName", source = "user.name")
    @Mapping(target = "activityName", source = "activity.name")
    @Mapping(target = "imageUrl", expression = "java(generateImageUrl(review))")
    ReviewDTO toDTO(Review review);

    // --- From DTO to Entity (Input) ---
    // MapStruct will automatically generate the code to convert the DTO back to the Entity
    Review toEntity(ReviewDTO dto);

    List<ReviewDTO> toDTOs(Collection<Review> reviews);

    // Generates the HTTP URL to access the image associated with the review
    default String generateImageUrl(Review review) {
        if (review.getImageFile() == null) return null; 
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/v1/reviews/")
                .path(review.getId().toString())
                .path("/image")
                .toUriString();
    }
}