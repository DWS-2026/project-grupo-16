package es.codeurjc.ferrumgym.controller;

import es.codeurjc.ferrumgym.model.Activity;
import es.codeurjc.ferrumgym.model.Booking;
import es.codeurjc.ferrumgym.model.SiteSettings;
import es.codeurjc.ferrumgym.model.User;
import es.codeurjc.ferrumgym.service.ActivityService;
import es.codeurjc.ferrumgym.service.BookingService;
import es.codeurjc.ferrumgym.service.ReviewService;
import es.codeurjc.ferrumgym.service.SiteSettingsService;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private ActivityService activityService;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private SiteSettingsService siteSettingsService; 

    @Autowired 
    private BookingService bookingService; 


    // --- DASHBOARD ---
    @GetMapping("/admin-dashboard")
    public String dashboard(Model model) {
        model.addAttribute("activities", activityService.findAll());
        model.addAttribute("users", userService.findAll());
        model.addAttribute("reviews", reviewService.findAll());

        List<User> allUsers = userService.findAll();
        Collections.reverse(allUsers); 
        List<User> recentUsers = allUsers.stream().limit(5).collect(Collectors.toList());

        model.addAttribute("recentUsers", recentUsers);
        model.addAttribute("adminName", "Admin");
        return "admin-dashboard";
    }

    // --- ADMIN CLASS MANAGEMENT ---
    @GetMapping("/admin-class")
    public String manageClassAttendance(@RequestParam(required = false) Long activityId, Model model) {

        List<Activity> allActivities = activityService.findAll();
        model.addAttribute("allActivities", allActivities);

        if (!allActivities.isEmpty()) {
            Activity selectedActivity;
            if (activityId != null) {
                selectedActivity = activityService.findById(activityId).orElse(allActivities.get(0));
            } else {
                selectedActivity = allActivities.get(0);
            }

            model.addAttribute("selectedActivity", selectedActivity);

            int percentage = 0;
            if (selectedActivity.getCapacity() > 0) {
                percentage = (selectedActivity.getEnrolled() * 100) / selectedActivity.getCapacity();
            }
            model.addAttribute("occupancyPercentage", percentage);

            List<Booking> bookings = bookingService.findByActivityId(selectedActivity.getId());
            model.addAttribute("bookings", bookings);
        }

        model.addAttribute("adminName", "Admin");
        return "admin-class";
    }

    @GetMapping("/admin-class/booking/delete/{bookingId}")
    public String deleteBooking(@PathVariable Long bookingId, @RequestParam Long activityId) {

        bookingService.deleteById(bookingId);

        return "redirect:/admin-class?activityId=" + activityId;
    }

    // --- SITE SETTINGS ---
    @GetMapping("/site-settings")
    public String settings(Model model) {
        SiteSettings settings = siteSettingsService.getSettings();
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
            java.security.Principal principal) {

        SiteSettings settings = siteSettingsService.getSettings();

        settings.setGymName(gymName);
        settings.setContactEmail(contactEmail);
        settings.setContactPhone(contactPhone);
        settings.setAddress(address);
        settings.setWeekdaysHours(weekdaysHours);
        settings.setWeekendsHours(weekendsHours);

        if (principal != null) {
            // Guardamos el nombre del admin directamente como String
            settings.setUpdatedBy(principal.getName());
        }

        siteSettingsService.saveSettings(settings);

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
            @RequestParam String description,
            @RequestParam("imageField") MultipartFile imageField,
            @RequestParam(value = "pdfFile", required = false) MultipartFile pdfFile) throws IOException {

        Activity newActivity = new Activity();

        newActivity.setName(name);
        newActivity.setTrainer(trainer);
        newActivity.setSchedule(schedule);
        newActivity.setCapacity(capacity);
        newActivity.setDescription(description);

        if (!imageField.isEmpty()) {
            newActivity.setImage(imageField.getBytes());
        }

        if (pdfFile != null && !pdfFile.isEmpty()) {
            String originalFilename = pdfFile.getOriginalFilename();
            newActivity.setPdfFilename(originalFilename);

            String uploadDir = "src/main/resources/static/docs/";
            Path uploadPath = Paths.get(uploadDir);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Path filePath = uploadPath.resolve(originalFilename);
            Files.copy(pdfFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
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
            @RequestParam String description,
            @RequestParam("imageField") MultipartFile imageField,
            @RequestParam(value = "pdfFile", required = false) MultipartFile pdfFile) throws IOException {

        Activity existingActivity = activityService.findById(id).orElse(null);

        if (existingActivity != null) {
            existingActivity.setName(name);
            existingActivity.setTrainer(trainer);
            existingActivity.setSchedule(schedule);
            existingActivity.setCapacity(capacity);
            existingActivity.setDescription(description);

            if (!imageField.isEmpty()) {
                existingActivity.setImage(imageField.getBytes());
            }

            if (pdfFile != null && !pdfFile.isEmpty()) {
                String originalFilename = pdfFile.getOriginalFilename();
                existingActivity.setPdfFilename(originalFilename);

                String uploadDir = "src/main/resources/static/docs/";
                Path uploadPath = Paths.get(uploadDir);

                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                Path filePath = uploadPath.resolve(originalFilename);
                Files.copy(pdfFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            }

            activityService.save(existingActivity);
        }

        return "redirect:/admin-dashboard";
    }
    
    // Delete users
    @GetMapping("/admin/user/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteById(id);
        return "redirect:/admin-users";
    }

    // Edit users (GET Method)
    @GetMapping("/admin/user/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        // Cambiado a userService
        User user = userService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));

        model.addAttribute("user", user);

        return "edit-user";
    }

    // Edit users (POST Method)
    @PostMapping("/admin/user/edit/{id}")
    public String updateUser(
            @PathVariable("id") Long id,
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam String role,
            @RequestParam("userAvatar") MultipartFile imageField) throws IOException {

        // Cambiado a userService
        User existingUser = userService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));

        existingUser.setName(name);
        existingUser.setEmail(email);

        existingUser.setRoles(new java.util.ArrayList<>(List.of(role)));

        if (!imageField.isEmpty()) {
            existingUser.setImage(imageField.getBytes());
        }

        // Cambiado a userService
        userService.save(existingUser);

        return "redirect:/admin-users";
    }

    // --- ADMIN USER MANAGEMENT ---
    @GetMapping("/admin-users")
    public String manageUsers(Model model) {
        model.addAttribute("users", userService.findAll());
        model.addAttribute("adminName", "Admin");
        return "admin-users";
    }

    // --- REVIEW MANAGEMENT ---
    @GetMapping("/review/delete/{id}")
    public String deleteReview(@PathVariable Long id) {
        reviewService.deleteById(id);
        return "redirect:/admin-dashboard";
    }

    // --- SAVE ATTENDANCE ---
    @PostMapping("/admin-class/attendance")
    public String saveAttendance(
            @RequestParam Long activityId,
            @RequestParam(required = false) List<Long> attendedBookingIds) {

        // Cambiado a bookingService
        List<Booking> bookings = bookingService.findByActivityId(activityId);

        for (Booking booking : bookings) {
            if (attendedBookingIds != null && attendedBookingIds.contains(booking.getId())) {
                booking.setAttended(true);
            } else {
                booking.setAttended(false);
            }

            // Cambiado a bookingService
            bookingService.save(booking);
        }

        return "redirect:/admin-class?activityId=" + activityId;
    }
}