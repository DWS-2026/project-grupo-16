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
        Activity activity;

        // 1. ¿Es una actualización? Intentamos recuperar la actividad original
        if (activityDto.getId() != null) {
            activity = activityRepository.findById(activityDto.getId())
                    .orElse(new Activity()); // Si no existe, creamos una nueva
        } else {
            activity = new Activity();
        }

        // 2. Volcamos los datos del DTO a la Entidad
        activity.setName(activityDto.getName());
        activity.setDescription(activityDto.getDescription());
        activity.setCapacity(activityDto.getCapacity());
        activity.setTrainer(activityDto.getTrainer());
        activity.setSchedule(activityDto.getSchedule());

        // Mantenemos el nombre del PDF si viene en el DTO
        if (activityDto.getPdfFilename() != null) {
            activity.setPdfFilename(activityDto.getPdfFilename());
        }

        // 3. Guardar (Spring decide si es INSERT o UPDATE por el ID)
        Activity savedActivity = activityRepository.save(activity);

        // 4. Devolver el resultado convertido de nuevo a DTO
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
