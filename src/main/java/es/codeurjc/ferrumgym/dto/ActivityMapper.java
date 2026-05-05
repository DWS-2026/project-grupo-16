package es.codeurjc.ferrumgym.dto;

import es.codeurjc.ferrumgym.model.Activity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Collection;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ActivityMapper {

    // --- De Entidad a DTO (Salida) ---
    @Mapping(target = "enrolledCount", source = "enrolled") // Mapeo de nombres distintos
    @Mapping(target = "imageUrl", expression = "java(generateImageUrl(activity))")
    ActivityDTO toDTO(Activity activity);

    // --- De DTO a Entidad (Entrada) ---
    // Hacemos el mapeo inverso: de enrolledCount a enrolled
    @Mapping(target = "enrolled", source = "enrolledCount")
    Activity toEntity(ActivityDTO dto);

    List<ActivityDTO> toDTOs(Collection<Activity> activities);

    //Genera la URL absoluta para la imagen de la actividad
    default String generateImageUrl(Activity activity) {
        if (activity.getImage() == null) return null;
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/v1/activities/")
                .path(activity.getId().toString())
                .path("/image")
                .toUriString();
    }
}