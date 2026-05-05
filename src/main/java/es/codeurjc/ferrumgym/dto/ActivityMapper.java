package es.codeurjc.ferrumgym.dto;

import es.codeurjc.ferrumgym.model.Activity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy; // Importante
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Collection;
import java.util.List;

// Añadimos unmappedTargetPolicy para que no se queje de lo que falte
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ActivityMapper {

    // --- De Entidad a DTO (Salida) ---
    // Cambiamos ActivityDTO por ActivityResponseDTO si seguiste mi consejo de nombres
    @Mapping(target = "enrolledCount", source = "enrolled") 
    @Mapping(target = "imageUrl", expression = "java(generateImageUrl(activity))")
    ActivityDTO toDTO(Activity activity);

    // --- De DTO a Entidad (Entrada) ---
    // ¡OJO! Ignoramos 'enrolled' porque es un campo calculado/relación que no tiene Setter
    @Mapping(target = "enrolled", ignore = true) 
    Activity toEntity(ActivityDTO dto);

    List<ActivityDTO> toDTOs(Collection<Activity> activities);

    default String generateImageUrl(Activity activity) {
        if (activity.getImage() == null) return null;
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/v1/activities/")
                .path(activity.getId().toString())
                .path("/image")
                .toUriString();
    }
}