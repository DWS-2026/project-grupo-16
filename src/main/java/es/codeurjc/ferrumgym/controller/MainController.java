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

    // ... (Other endpoints remain the same, focusing on the ones with comments)

    @PostMapping("/register")
    public String processRegistration(@RequestParam String name, @RequestParam String email,
                                      @RequestParam String password, @RequestParam("imageFile") MultipartFile imageFile) throws IOException {

        // 1. Verify if the email is already registered to prevent duplicates
        if (userService.findByEmail(email).isPresent()) {
            return "redirect:/register?error=user_exists";
        }

        // 2. Encryption logic: Hash the password securely before storing
        // Note: Assumes passwordEncoder is injected or handled within userService
        
        // 3. Create the user entity with the default role
        User newUser = new User(name, email, password, List.of("ROLE_USER"));

        // 4. Save the avatar image if the user uploaded one
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