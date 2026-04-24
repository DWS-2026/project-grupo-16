package es.codeurjc.ferrumgym.service;

import es.codeurjc.ferrumgym.model.Activity;
import es.codeurjc.ferrumgym.repository.ActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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

    public void save(Activity activity) {
        activityRepository.save(activity);
    }

    public void deleteById(Long id) {
        activityRepository.deleteById(id);
    }

	public Page<Activity> findAll(Pageable pageable) {
    return activityRepository.findAll(pageable);
}
}
