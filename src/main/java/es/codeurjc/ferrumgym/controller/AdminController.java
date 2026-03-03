package es.codeurjc.ferrumgym.controller;

import es.codeurjc.ferrumgym.model.Activity;
import es.codeurjc.ferrumgym.service.ActivityService;
import es.codeurjc.ferrumgym.service.ReviewService;
import es.codeurjc.ferrumgym.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class AdminController {

    // 1. Ahora inyectamos los SERVICES en lugar de los Repositories
    @Autowired
    private UserService userService;

    @Autowired
    private ActivityService activityService;

    @Autowired
    private ReviewService reviewService;

    // Dashboard
    @GetMapping("/admin-dashboard")
    public String dashboard(Model model) {
        // Fetch all activities, users and reviews from the SERVICES
        model.addAttribute("activities", activityService.findAll());
        model.addAttribute("users", userService.findAll());
        model.addAttribute("reviews", reviewService.findAll());

        model.addAttribute("adminName", "Admin"); // For the navbar

        return "admin-dashboard";
    }

    // Admin class management page
    @GetMapping("/admin-class")
    public String users(Model model) {
        // Fetch users from the service
        model.addAttribute("users", userService.findAll());
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

    // 2. POST: Receive data, create the activity and save it via Service
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

        activityService.save(newActivity);

        return "redirect:/admin-dashboard";
    }

    // GET: Delete an activity by its ID
    @GetMapping("/activity/delete/{id}")
    public String deleteActivity(@PathVariable Long id) {
        // Delete the activity via service
        activityService.deleteById(id);

        // Redirect back to the dashboard
        return "redirect:/admin-dashboard";
    }

    @GetMapping("/activity/edit/{id}")
    public String editActivityForm(@PathVariable Long id, Model model) {

        // Find the activity via service. If not found, go back to dashboard.
        Activity activityToEdit = activityService.findById(id).orElse(null);
        if (activityToEdit == null) {
            return "redirect:/admin-dashboard";
        }

        // Send the specific activity data to the HTML
        model.addAttribute("activity", activityToEdit);
        model.addAttribute("adminName", "Admin");

        return "activity-edit"; 
    }

    // POST: Save the edited changes via Service
    @PostMapping("/activity/edit/{id}")
    public String updateActivity(
            @PathVariable Long id,
            @RequestParam String name,
            @RequestParam String trainer,
            @RequestParam String schedule,
            @RequestParam int capacity,
            @RequestParam int enrolled,
            @RequestParam String description,
            @RequestParam("imageField") MultipartFile imageField) throws IOException {

        // Find the original activity
        Activity existingActivity = activityService.findById(id).orElse(null);

        if (existingActivity != null) {
            // Update fields
            existingActivity.setName(name);
            existingActivity.setTrainer(trainer);
            existingActivity.setSchedule(schedule);
            existingActivity.setCapacity(capacity);
            existingActivity.setEnrolled(enrolled);
            existingActivity.setDescription(description);

            // ONLY update the image if the admin uploaded a new one
            if (!imageField.isEmpty()) {
                existingActivity.setImage(imageField.getBytes());
            }

            // Save the updated activity
            activityService.save(existingActivity);
        }

        return "redirect:/admin-dashboard";
    }
}
