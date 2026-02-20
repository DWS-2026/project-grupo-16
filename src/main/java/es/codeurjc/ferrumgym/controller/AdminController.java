package es.codeurjc.ferrumgym.controller;

import es.codeurjc.ferrumgym.repository.ActivityRepository;
import es.codeurjc.ferrumgym.repository.ReviewRepository;
import es.codeurjc.ferrumgym.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import es.codeurjc.ferrumgym.model.Activity;

@Controller
public class AdminController {

    // MySQL connections
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private ReviewRepository reviewRepository;


    // Dashboard
    @GetMapping("/admin-dashboard")
    public String dashboard(Model model) {

        // Fetch all activities, users and reviews from the database
        model.addAttribute("activities", activityRepository.findAll());
        model.addAttribute("users", userRepository.findAll());
        model.addAttribute("reviews", reviewRepository.findAll());

        model.addAttribute("adminName", "Admin"); // For the navbar

        return "admin-dashboard";
    }

    // Admin class management page
    @GetMapping("/admin-class")
    public String users(Model model) {

        // Fetch users from the database
        model.addAttribute("users", userRepository.findAll());
        model.addAttribute("adminName", "Admin");

        return "admin-class";
    }

    // Site settings page
    @GetMapping("/site-settings")
    public String settings(Model model) {
        model.addAttribute("adminName", "Admin");
        return "site-settings";
    }

    // 1. GET: Show the empty form when clicking the button
    @GetMapping("/activity/new")
    public String newActivityForm(Model model) {
        model.addAttribute("adminName", "Admin");
        return "activity-form";
    }

    // 2. POST: Receive data, create the activity and save it to the DB
    @PostMapping("/activity/new")
    public String saveActivity(
            @RequestParam String name,
            @RequestParam String trainer,
            @RequestParam String schedule,
            @RequestParam int capacity,
            @RequestParam int enrolled,
            @RequestParam String description,
            @RequestParam("imageField") MultipartFile imageField) throws IOException {

        Activity newActivity = new Activity();

        newActivity.setName(name);
        newActivity.setTrainer(trainer);
        newActivity.setSchedule(schedule);
        newActivity.setCapacity(capacity);
        newActivity.setEnrolled(enrolled);
        newActivity.setDescription(description);

        if (!imageField.isEmpty()) {
            newActivity.setImage(imageField.getBytes());
        }

        activityRepository.save(newActivity);

        return "redirect:/admin-dashboard";
    }
}
