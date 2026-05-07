package es.codeurjc.ferrumgym.dto;

import es.codeurjc.ferrumgym.model.Activity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy; 
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Collection;
import java.util.List;

// Add unmappedTargetPolicy to ignore missing target properties during mapping and prevent warnings
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ActivityMapper {

    // --- From Entity to DTO (Output) ---
    // Maps the internal 'enrolled' integer to the DTO's 'enrolledCount'
    @Mapping(target = "enrolledCount", source = "enrolled") 
    @Mapping(target = "imageUrl", expression = "java(generateImageUrl(activity))")
    ActivityDTO toDTO(Activity activity);

    // --- From DTO to Entity (Input) ---
    // WARNING: 'enrolled' is ignored because it is a calculated field/relationship that lacks a Setter
    @Mapping(target = "enrolled", ignore = true) 
    Activity toEntity(ActivityDTO dto);

    // Utility method to map a collection of activities to a list of DTOs
    List<ActivityDTO> toDTOs(Collection<Activity> activities);

    // Custom logic to generate the URL for accessing the activity's main image
    default String generateImageUrl(Activity activity) {
        if (activity.getImage() == null) return null;
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/v1/activities/")
                .path(activity.getId().toString())
                .path("/image")
                .toUriString();
    }
}