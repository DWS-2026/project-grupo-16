package es.codeurjc.ferrumgym.service;

import es.codeurjc.ferrumgym.model.Activity;
import es.codeurjc.ferrumgym.repository.ActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class ActivityService {

    @Autowired
    private ActivityRepository activityRepository;

    // --- MÉTODOS DE BÚSQUEDA ---

    public List<Activity> findAll() {
        return activityRepository.findAll();
    }

    public Page<Activity> findAll(Pageable pageable) {
        return activityRepository.findAll(pageable);
    }

    public Optional<Activity> findById(Long id) {
        return activityRepository.findById(id);
    }

    // Guarda la actividad. Recibe y devuelve la entidad real
    public Activity save(Activity activity) {
        return activityRepository.save(activity);
    }

    // Borra la actividad lanzando 404 si no existe
    public void deleteById(Long id) {
        if (!activityRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "La actividad no existe");
        }
        activityRepository.deleteById(id);
    }

    // Gestiona la imagen de la actividad trabajando con el objeto del modelo
    public void saveImage(Activity activity, MultipartFile imageFile) throws IOException {
        activity.setImage(imageFile.getBytes());
        activityRepository.save(activity);
    }

    // --- MÉTODOS DE ACTUALIZACIÓN ---

    public Activity update(Long id, Activity updatedActivity) {
        // 1. Buscamos la actividad actual en la base de datos
        Activity existingActivity = activityRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Activity not found"));

        // 2. Actualizamos los campos con la nueva información
        existingActivity.setName(updatedActivity.getName());
        existingActivity.setDescription(updatedActivity.getDescription());
        existingActivity.setCapacity(updatedActivity.getCapacity());
        existingActivity.setTrainer(updatedActivity.getTrainer());
        existingActivity.setSchedule(updatedActivity.getSchedule());

        // 3. PROTECCIÓN DE IMAGEN: Si la nueva entidad no trae imagen (lo normal en un
        // JSON),
        // mantenemos la que ya teníamos guardada para no borrarla.
        if (updatedActivity.getImage() != null && updatedActivity.getImage().length > 0) {
            existingActivity.setImage(updatedActivity.getImage());
        }

        // 4. Guardamos la actividad ya actualizada
        return activityRepository.save(existingActivity);
    }
}