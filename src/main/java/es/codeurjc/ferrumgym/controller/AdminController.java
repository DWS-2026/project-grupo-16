package es.codeurjc.ferrumgym.controller;

import es.codeurjc.ferrumgym.model.Activity;
import es.codeurjc.ferrumgym.model.Booking;
import es.codeurjc.ferrumgym.model.SiteSettings;
import es.codeurjc.ferrumgym.repository.SiteSettingsRepository;
import es.codeurjc.ferrumgym.service.ActivityService;
import es.codeurjc.ferrumgym.service.ReviewService;
import es.codeurjc.ferrumgym.service.UserService;
import es.codeurjc.ferrumgym.model.User;
import es.codeurjc.ferrumgym.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Controller
public class AdminController {

    @Autowired
    private UserService userService;

	@Autowired
    private UserRepository userRepository;

    @Autowired
    private ActivityService activityService;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private SiteSettingsRepository siteSettingsRepository;

    @Autowired
    private es.codeurjc.ferrumgym.repository.BookingRepository bookingRepository;


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

            // Buscamos a los usuarios apuntados
            List<Booking> bookings = bookingRepository.findByActivityId(selectedActivity.getId());
            model.addAttribute("bookings", bookings);
        }

        model.addAttribute("adminName", "Admin");
        return "admin-class";
    }

@GetMapping("/admin-class/booking/delete/{bookingId}")
    public String deleteBooking(@PathVariable Long bookingId, @RequestParam Long activityId) {

        Activity activity = activityService.findById(activityId).orElse(null);
        if (activity != null && activity.getEnrolled() > 0) {
            activity.setEnrolled(activity.getEnrolled() - 1);
            activityService.save(activity); // Guardamos el nuevo número
        }

        // Delete the booking from the database
        bookingRepository.deleteById(bookingId);

        // Redirect back to the class management page for the same activity
        return "redirect:/admin-class?activityId=" + activityId;
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
            @RequestParam String weekendsHours){

        SiteSettings settings = siteSettingsRepository.findAll().stream().findFirst().orElse(new SiteSettings());

        settings.setGymName(gymName);
        settings.setContactEmail(contactEmail);
        settings.setContactPhone(contactPhone);
        settings.setAddress(address);
        settings.setWeekdaysHours(weekdaysHours);
        settings.setWeekendsHours(weekendsHours);

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
	// Delete users
    @GetMapping("/admin/user/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteById(id); //
        return "redirect:/admin-users";
    }

	// Edit users (GET Method)

	@GetMapping("/admin/user/edit/{id}")
public String showEditForm(@PathVariable("id") Long id, Model model) {
    //Searching for the user by ID, if not found, throw an exception
    User user = userRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));

    // Transfer the user data to the model to pre-fill the form
    model.addAttribute("user", user);

    return "edit-user";
}
 // Edit users (POST Method)
@PostMapping("/admin/user/edit/{id}")
public String updateUser(@PathVariable("id") Long id, @ModelAttribute("user") User user) {
    // We set the ID to ensure we update the existing user instead of creating a new one
    user.setId(id);
    userRepository.save(user);

    return "redirect:/admin-dashboard";
}

	// --- ADMIN USER MANAGEMENT ---
    @GetMapping("/admin-users")
    public String manageUsers(Model model) {
        // Show all users in the system
        model.addAttribute("users", userService.findAll());
        model.addAttribute("adminName", "Admin");
        return "admin-users";
    }

	// --- REVIEW MANAGEMENT ---
    @GetMapping("/review/delete/{id}")
    public String deleteReview(@PathVariable Long id) {
        // ReviewService for delete review by id
        reviewService.deleteById(id);
        return "redirect:/admin-dashboard";
    }

	// --- SAVE ATTENDANCE ---
    @PostMapping("/admin-class/attendance")
    public String saveAttendance(
            @RequestParam Long activityId,
            @RequestParam(required = false) List<Long> attendedBookingIds) {

        // Load all bookings for this activity
        List<Booking> bookings = bookingRepository.findByActivityId(activityId);

        // Check each booking against the list of attended IDs and update the "attended" status accordingly
        for (Booking booking : bookings) {
            // If the booking ID is in the list of attended IDs, mark it as attended
            if (attendedBookingIds != null && attendedBookingIds.contains(booking.getId())) {
                booking.setAttended(true);
            } else {
                // If it's not in the list, mark it as not attended
                booking.setAttended(false);
            }

            // Save the updated booking back to the database
            bookingRepository.save(booking);
        }

        return "redirect:/admin-class?activityId=" + activityId;
    }
}
