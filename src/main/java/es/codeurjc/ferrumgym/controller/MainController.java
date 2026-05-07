package es.codeurjc.ferrumgym.controller;

import es.codeurjc.ferrumgym.model.Activity;
import es.codeurjc.ferrumgym.model.Booking;
import es.codeurjc.ferrumgym.model.User;
import es.codeurjc.ferrumgym.service.ActivityService;
import es.codeurjc.ferrumgym.service.BookingService;
import es.codeurjc.ferrumgym.service.UserService;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.Optional;
import java.util.List;

@Controller
public class MainController {

    @Autowired
    private ActivityService activityService;

    @Autowired
    private UserService userService;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @GetMapping("/")
    public String index(Model model) {
        // Activities
        model.addAttribute("activities", activityService.findAll());
        return "index";
    }

    @GetMapping("/prices")
    public String prices() {
        return "prices";
    }

    @GetMapping("/activity/{id}")
    public String activityDetail(Model model, @PathVariable long id, @RequestParam(required = false) String error) {
        Optional<Activity> activity = activityService.findById(id);
        if (activity.isPresent()) {
            model.addAttribute("activity", activity.get());

            if ("already_booked".equals(error)) {
                model.addAttribute("errorMessage", "You are already enrolled in this class!");
            }

            return "activity-detail";
        } else {
            return "error/404";
        }
    }

    @GetMapping("/activity/{id}/image")
    public ResponseEntity<Object> downloadImage(@PathVariable long id) {
        Optional<Activity> activity = activityService.findById(id);
        if (activity.isPresent() && activity.get().getImage() != null) {
            return ResponseEntity.ok()
                    .header(org.springframework.http.HttpHeaders.CONTENT_TYPE, "image/jpeg")
                    .body(activity.get().getImage());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/activity/{id}/pdf")
    public ResponseEntity<org.springframework.core.io.Resource> downloadPdf(@PathVariable long id) throws java.net.MalformedURLException {
    
    Activity activity = activityService.findById(id).orElseThrow();
    String fileName = activity.getPdfFilename();

    if (fileName == null || fileName.isEmpty()) {
        return ResponseEntity.notFound().build();
    }

    
    Path filePath = Paths.get("uploads/docs/").resolve(fileName);
    org.springframework.core.io.Resource pdf = new org.springframework.core.io.UrlResource(filePath.toUri());

    return ResponseEntity.ok()
            .header(org.springframework.http.HttpHeaders.CONTENT_TYPE, "application/pdf")
            .body(pdf);
}

// Booking Controller POST method
    @PostMapping("/activity/{id}/book")
    public String bookClass(@PathVariable Long id, Principal principal, Model model) {
        Activity activity = activityService.findById(id).orElseThrow();
        String email = principal.getName();
        User currentUser = userService.findByEmail(email).orElseThrow();

        boolean isEnrolled = false;
        for (Booking b : activity.getBookings()) {
            if (b.getUser().getId().equals(currentUser.getId())) {
                isEnrolled = true;
                break;
            }
        }

        if (isEnrolled) {
            // Redirect to the same page with an error message indicating the user is already enrolled
            return "redirect:/activity/" + id + "?error=already_booked";
        } else {
            if (activity.getEnrolled() < activity.getCapacity()) {
                Booking newBooking = new Booking();
                newBooking.setUser(currentUser);
                newBooking.setActivity(activity);

                bookingService.save(newBooking);

                // Redirect to the same page + id
                return "redirect:/activity/" + id;
            } else {
                // Case when the class is full
                return "redirect:/activity/" + id + "?error=class_full";
            }
        }
    }


    @GetMapping("/user/{id}/image")
    public ResponseEntity<Object> downloadUserImage(@PathVariable long id) {
        Optional<User> user = userService.findById(id);
        if (user.isPresent() && user.get().getImage() != null) {
            return ResponseEntity.ok()
                    .header(org.springframework.http.HttpHeaders.CONTENT_TYPE, "image/jpeg")
                    .body(user.get().getImage());
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/user-profile")
    public String userProfile(Model model, HttpServletRequest request) {

        String email = request.getUserPrincipal().getName();

        User currentUser = userService.findByEmail(email).orElseThrow();

        model.addAttribute("user", currentUser);

        List<Booking> myBookings = currentUser.getBookings();
        model.addAttribute("bookings", myBookings);
        model.addAttribute("bookingCount", myBookings.size());

        return "user-profile";
    }

    @GetMapping("/edit-profile")
    public String editProfile(Model model, Principal principal) {
    
        String email = principal.getName();
        
        User currentUser = userService.findByEmail(email).orElseThrow();

        model.addAttribute("user", currentUser);
        return "edit-profile";
    }

    @PostMapping("/edit-profile/save")
    public String saveProfile(
            @RequestParam String fullName,
            @RequestParam String userEmail,
            @RequestParam("userAvatar") MultipartFile imageFile,
            Principal principal) throws IOException {

        String currentEmail = principal.getName();
        User currentUser = userService.findByEmail(currentEmail).orElseThrow();

        currentUser.setName(fullName);
        currentUser.setEmail(userEmail);

        if (!imageFile.isEmpty()) {
            currentUser.setImage(imageFile.getBytes());
        }

        userService.save(currentUser);
        return "redirect:/user-profile";
    }

@GetMapping("/booking/cancel/{id}")
    public String cancelBooking(@PathVariable Long id, Principal principal) {

        Optional<Booking> bookingOpt = bookingService.findById(id);

        // If the booking doesn't exist, show 404
        if (bookingOpt.isEmpty()) {
            return "error/404";
        }

        Booking booking = bookingOpt.get();

        // IDOR Protection: only the user who made the booking can cancel it
        String currentUserEmail = principal.getName();
        if (!booking.getUser().getEmail().equals(currentUserEmail)) {
            return "error/403";
        }

        //If we reach this point, it means the user is authorized to cancel the booking
        bookingService.deleteById(id);

        return "redirect:/user-profile";
    }

    @GetMapping("/login")
    public String login(Model model, @RequestParam(required = false) String error) {
        if (error != null) {
            model.addAttribute("loginError", true);
            model.addAttribute("errorMessage", "Invalid email or password. Please try again.");
        }
        return "login";
    }

    @GetMapping("/register")
    public String registerPage(Model model, @RequestParam(required = false) String error) {
        if ("user_exists".equals(error)) {
            model.addAttribute("registerError", true);
            model.addAttribute("errorMessage", "An account with this email already exists.");
        }
            return "register";
    }

   
    @PostMapping("/register")
    public String registerUser(@RequestParam String name,
                           @RequestParam String email,
                           @RequestParam String password,
                           @RequestParam("formFile") MultipartFile imageFile) throws IOException {

        // 1. Verify if the email is already registered to prevent duplicates
        if (userService.findByEmail(email).isPresent()) {
            return "redirect:/register?error=user_exists";
        }

       
        String encodedPassword = passwordEncoder.encode(password);

    
        User newUser = new User(name, email, encodedPassword, List.of("ROLE_USER"));


        if (!imageFile.isEmpty()) {
            newUser.setImage(imageFile.getBytes());
        }

        // 5. Persist the user and redirect to the login page
        userService.save(newUser);
        return "redirect:/login";
    }

    @GetMapping("/forgot-password")
    public String forgotPassword() {
        return "forgot-password"; // Loads the forgot-password.html template
    }

    @PostMapping("/forgot-password")
    public String processRecovery(@RequestParam String email, Model model) {

        // 1. Check if the provided email exists in the database
        boolean userExists = userService.findByEmail(email).isPresent();

        if (userExists) {
            // Displays a success message to the user
            model.addAttribute("success", true);
            model.addAttribute("message", "A password reset link has been sent to " + email);
        } else {
            // Displays an appropriate error message if the account is not found
            model.addAttribute("error", true);
            model.addAttribute("message", "We couldn't find an account with that email address.");
        }

        return "forgot-password";
    }

    @GetMapping("/download/pdf/{fileName}")
    public ResponseEntity<org.springframework.core.io.Resource> downloadPdf(@PathVariable String fileName) {
        try {
            Path path = Paths.get("uploads/docs/").resolve(fileName);
            org.springframework.core.io.Resource resource = new org.springframework.core.io.UrlResource(path.toUri());

            if (resource.exists()) {
                return ResponseEntity.ok()
                        .header(org.springframework.http.HttpHeaders.CONTENT_TYPE, "application/pdf")
                        .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION,
                                "inline; filename=\"" + fileName + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}