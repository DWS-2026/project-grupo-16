package es.codeurjc.ferrumgym.controller;

import es.codeurjc.ferrumgym.model.Activity;
import es.codeurjc.ferrumgym.model.SiteSettings;
import es.codeurjc.ferrumgym.repository.SiteSettingsRepository;
import es.codeurjc.ferrumgym.service.ActivityService;
import es.codeurjc.ferrumgym.service.ReviewService;
import es.codeurjc.ferrumgym.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
public class AdminController {

    // 1. Usamos los SERVICES de tus compañeros para lo que ya existía
    @Autowired
    private UserService userService;

    @Autowired
    private ActivityService activityService;

    @Autowired
    private ReviewService reviewService;

    // 2. Usamos TU REPOSITORY para la función nueva que acabas de crear
    @Autowired
    private SiteSettingsRepository siteSettingsRepository;


    // --- DASHBOARD ---
    @GetMapping("/admin-dashboard")
    public String dashboard(Model model) {
        model.addAttribute("activities", activityService.findAll());
        model.addAttribute("users", userService.findAll());
        model.addAttribute("reviews", reviewService.findAll());

        model.addAttribute("adminName", "Admin");
        return "admin-dashboard";
    }

    // --- ADMIN CLASS MANAGEMENT ---
    @GetMapping("/admin-class")
    public String users(Model model) {
        model.addAttribute("users", userService.findAll());
        model.addAttribute("adminName", "Admin");
        return "admin-class";
    }

    // --- SITE SETTINGS ---
    @GetMapping("/site-settings")
    public String settings(Model model) {
        SiteSettings settings = siteSettingsRepository.findAll().stream().findFirst().orElse(null);

        model.addAttribute("settings", settings);
        model.addAttribute("adminName", "Admin");
        return "site-settings";
    }

    @PostMapping("/site-settings")
    public String updateSettings(
            @RequestParam String gymName,
            @RequestParam String contactEmail,
            @RequestParam String contactPhone,
            @RequestParam String address,
            @RequestParam String weekdaysHours,
            @RequestParam String weekendsHours,
            @RequestParam(required = false) String maintenanceMode) {

        SiteSettings settings = siteSettingsRepository.findAll().stream().findFirst().orElse(new SiteSettings());

        settings.setGymName(gymName);
        settings.setContactEmail(contactEmail);
        settings.setContactPhone(contactPhone);
        settings.setAddress(address);
        settings.setWeekdaysHours(weekdaysHours);
        settings.setWeekendsHours(weekendsHours);
        settings.setMaintenanceMode(maintenanceMode != null);

        siteSettingsRepository.save(settings);

        return "redirect:/site-settings";
    }

    // --- ACTIVITY MANAGEMENT ---
    @GetMapping("/activity/new")
    public String newActivityForm(Model model) {
        model.addAttribute("adminName", "Admin");
        return "activity-form";
    }

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

    @GetMapping("/activity/delete/{id}")
    public String deleteActivity(@PathVariable Long id) {
        activityService.deleteById(id);
        return "redirect:/admin-dashboard";
    }

    @GetMapping("/activity/edit/{id}")
    public String editActivityForm(@PathVariable Long id, Model model) {

        Activity activityToEdit = activityService.findById(id).orElse(null);
        if (activityToEdit == null) {
            return "redirect:/admin-dashboard";
        }

        model.addAttribute("activity", activityToEdit);
        model.addAttribute("adminName", "Admin");

        return "activity-edit";
    }

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

        Activity existingActivity = activityService.findById(id).orElse(null);

        if (existingActivity != null) {
            existingActivity.setName(name);
            existingActivity.setTrainer(trainer);
            existingActivity.setSchedule(schedule);
            existingActivity.setCapacity(capacity);
            existingActivity.setEnrolled(enrolled);
            existingActivity.setDescription(description);

            if (!imageField.isEmpty()) {
                existingActivity.setImage(imageField.getBytes());
            }

            activityService.save(existingActivity);
        }

        return "redirect:/admin-dashboard";
    }

    @GetMapping("/admin/user/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteById(id); //
        return "redirect:/admin-class";
    }
}
