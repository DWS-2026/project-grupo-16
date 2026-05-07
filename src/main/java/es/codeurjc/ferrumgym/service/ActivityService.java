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

    // --- SEARCH METHODS ---

    public List<Activity> findAll() {
        return activityRepository.findAll();
    }

    public Page<Activity> findAll(Pageable pageable) {
        return activityRepository.findAll(pageable);
    }

    public Optional<Activity> findById(Long id) {
        return activityRepository.findById(id);
    }

    // Saves the activity. Receives and returns the actual entity
    public Activity save(Activity activity) {
        return activityRepository.save(activity);
    }

    // Deletes the activity, throwing a 404 error if it does not exist
    public void deleteById(Long id) {
        if (!activityRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "La actividad no existe");
        }
        activityRepository.deleteById(id);
    }

    // Manages the activity image by working directly with the model object
    public void saveImage(Activity activity, MultipartFile imageFile) throws IOException {
        activity.setImage(imageFile.getBytes());
        activityRepository.save(activity);
    }

    // --- UPDATE METHODS ---

    public Activity update(Long id, Activity updatedActivity) {
        // 1. Find the current activity in the database
        Activity existingActivity = activityRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Activity not found"));

        // 2. Update the fields with the new information
        existingActivity.setName(updatedActivity.getName());
        existingActivity.setDescription(updatedActivity.getDescription());
        existingActivity.setCapacity(updatedActivity.getCapacity());
        existingActivity.setTrainer(updatedActivity.getTrainer());
        existingActivity.setSchedule(updatedActivity.getSchedule());

        // 3. IMAGE PROTECTION: If the new entity does not include an image (common in JSON updates),
        // keep the existing one to prevent unintended deletion.
        if (updatedActivity.getImage() != null && updatedActivity.getImage().length > 0) {
            existingActivity.setImage(updatedActivity.getImage());
        }

        // 4. Save the updated activity
        return activityRepository.save(existingActivity);
    }
}