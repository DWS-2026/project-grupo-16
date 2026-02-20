package es.codeurjc.ferrumgym.controller;

import es.codeurjc.ferrumgym.repository.ActivityRepository;
import es.codeurjc.ferrumgym.repository.ReviewRepository;
import es.codeurjc.ferrumgym.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {

    // MySql connections
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private ReviewRepository reviewRepository;


    // Dashboard
    @GetMapping("/admin-dashboard")
    public String dashboard(Model model) {

        // Download all activities, users and reviews to show in the dashboard from the database
        model.addAttribute("activities", activityRepository.findAll());
        model.addAttribute("users", userRepository.findAll());
        model.addAttribute("reviews", reviewRepository.findAll());

        model.addAttribute("adminName", "Admin"); // Para el navbar

        return "admin-dashboard";
    }

    // Admin class management page
    @GetMapping("/admin-class")
    public String users(Model model) {

        // Sacamos los usuarios de la BD
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

    // Form to create a new activity
    @GetMapping("/admin/activity/new")
    public String newActivityForm(Model model) {
        model.addAttribute("adminName", "Admin");
        return "activity-form";
    }
}
