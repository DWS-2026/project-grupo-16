package es.codeurjc.ferrumgym.controller;

import es.codeurjc.ferrumgym.model.Activity;
import es.codeurjc.ferrumgym.repository.ActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;

@Controller
public class MainController {

    @Autowired
    private ActivityRepository activityRepository;

    @GetMapping("/")
    public String index(Model model) {
        // Pasa la lista de actividades a la vista "index.html"
        model.addAttribute("activities", activityRepository.findAll());
        return "index";
    }

    @GetMapping("/activity/{id}")
    public String activityDetail(Model model, @PathVariable long id) {
        Optional<Activity> activity = activityRepository.findById(id);
        if (activity.isPresent()) {
            model.addAttribute("activity", activity.get());
            return "activity-detail"; 
        } else {
            return "404";
        }
    }

    @GetMapping("/activity/{id}/image")
    public ResponseEntity<Object> downloadImage(@PathVariable long id) {
        Optional<Activity> activity = activityRepository.findById(id);
        if (activity.isPresent() && activity.get().getImage() != null) {
            return ResponseEntity.ok()
                    .header(org.springframework.http.HttpHeaders.CONTENT_TYPE, "image/jpeg")
                    .body(activity.get().getImage());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}