package es.codeurjc.ferrumgym.service;

import es.codeurjc.ferrumgym.dto.ActivityDTO;
import es.codeurjc.ferrumgym.model.Activity;
import es.codeurjc.ferrumgym.repository.ActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;

@Service
public class ActivityService {

    @Autowired
    private ActivityRepository activityRepository;

    public List<Activity> findAll() {
        return activityRepository.findAll();
    }

    public Optional<Activity> findById(Long id) {
        return activityRepository.findById(id);
    }

    public ActivityDTO save(ActivityDTO activityDto) {
        // 1. Crear la entidad a partir del DTO (Conversión DTO -> Entidad)
        Activity activity = new Activity();

        // Si el DTO tiene ID, es una actualización; si no, es una creación
        if (activityDto.getId() != null) {
            activity.setId(activityDto.getId());
        }

        activity.setName(activityDto.getName());
        activity.setDescription(activityDto.getDescription());
        activity.setCapacity(activityDto.getCapacity());
        // Añade aquí el resto de campos que tenga tu entidad Activity

        // 2. Guardar la entidad en la base de datos usando el repositorio
        Activity savedActivity = activityRepository.save(activity);

        // 3. Devolver el resultado convertido de nuevo a DTO (Conversión Entidad ->
        // DTO)
        return new ActivityDTO(savedActivity);
    }

    public void deleteById(Long id) {
        // Si no existe, lanzamos 404 en JSON (Punto 12)
        if (!activityRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "La actividad no existe");
        }
        activityRepository.deleteById(id);
    }

    public Page<Activity> findAll(Pageable pageable) {
        return activityRepository.findAll(pageable);
    }

    public ActivityDTO findByIdDTO(Long id) {
        return activityRepository.findById(id)
            .map(ActivityDTO::new)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Actividad no encontrada"));
    }
}
