package es.codeurjc.ferrumgym.controller;

import es.codeurjc.ferrumgym.model.Activity;
import es.codeurjc.ferrumgym.model.Booking;
import es.codeurjc.ferrumgym.model.Review;
import es.codeurjc.ferrumgym.model.User;
import es.codeurjc.ferrumgym.service.ActivityService;
import es.codeurjc.ferrumgym.service.BookingService;
import es.codeurjc.ferrumgym.service.ReviewService;
import es.codeurjc.ferrumgym.service.UserService;

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
import java.util.Optional;
import java.util.List;

@Controller
public class MainController {

    @Autowired
    private ActivityService activityService;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private UserService userService;

    @Autowired
    private BookingService bookingService;

    @GetMapping("/")
    public String index(Model model) {
        // Activities
        model.addAttribute("activities", activityService.findAll());
        return "index";
    }

    @GetMapping("/activity/{id}")
    public String activityDetail(Model model, @PathVariable long id) {
        Optional<Activity> activity = activityService.findById(id);
        if (activity.isPresent()) {
            model.addAttribute("activity", activity.get());
            return "activity-detail";
        } else {
            return "404";
        }
    }

    // POST Method to handle the "Add Review" form submission
    @PostMapping("/activity/{id}/review")
    public String addReview(@PathVariable long id, @RequestParam String comment, @RequestParam int rating) {
        Optional<Activity> activity = activityService.findById(id);

        if (activity.isPresent()) {
            Review review = new Review();
            review.setComment(comment);
            review.setRating(rating);
            review.setActivity(activity.get());

            // TODO: Cuando implementes Spring Security, aquí cogeremos el usuario de la sesión.
            // Usamos userService en lugar de userRepository
            User user = userService.findById(2L).orElse(null);
            review.setUser(user);

            // Usamos reviewService en lugar de reviewRepository
            reviewService.save(review);
        }

        // Redirige de vuelta a la página de detalles para ver la nueva reseña publicada
        return "redirect:/activity/" + id;
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

    // Método POST para procesar el botón de "Book Class"
    @PostMapping("/activity/{id}/book")
    public String bookActivity(@PathVariable long id) {
        Optional<Activity> activityOpt = activityService.findById(id);

        if (activityOpt.isPresent()) {
            Activity activity = activityOpt.get();

            // Comprobamos que no esté llena por seguridad
            if (!activity.isFull()) {
                Booking booking = new Booking();
                booking.setActivity(activity);
                booking.setBookingDate(java.time.LocalDateTime.now());

                // TODO: Cuando implementes Spring Security, aquí cogeremos el usuario de la sesión.
                // Simulamos el usuario 1L ("Juan Perez")
                User user = userService.findById(2L).orElse(null);
                booking.setUser(user);

                // 1. Guardamos la reserva usando el nuevo BookingService
                bookingService.save(booking);

                // 2. Aumentamos en 1 el número de apuntados a la clase y actualizamos la actividad
                activity.setEnrolled(activity.getEnrolled() + 1);
                activityService.save(activity);
            }
        }

        // Redirigimos a la página de la actividad para que vea la barra de progreso actualizada
        return "redirect:/activity/" + id;
    } // <-- ¡Esta es la llave que te faltaba y que rompía todo!

    //Controlador de registros de usuario
    @PostMapping("/register")
    public String registerUser(@RequestParam String name, @RequestParam String email, @RequestParam String password,
            @RequestParam("formFile") MultipartFile imageFile) throws IOException {

        // Creamos el usuario con el rol por defecto de cliente
        User newUser = new User(name, email, password, List.of("ROLE_USER"));

        // Guardamos la foto si la ha subido
        if (!imageFile.isEmpty()) {
            newUser.setImage(imageFile.getBytes());
        }

        userService.save(newUser); // Usamos tu servicio
        return "redirect:/login";
    }

    //Gestion de imagenes de usuario
    @GetMapping("/user/{id}/image")
    public ResponseEntity<Object> downloadUserImage(@PathVariable long id) {
        Optional<User> user = userService.findById(id); //
        if (user.isPresent() && user.get().getImage() != null) {
            return ResponseEntity.ok()
                    .header(org.springframework.http.HttpHeaders.CONTENT_TYPE, "image/jpeg")
                    .body(user.get().getImage());
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/user-profile")
    public String userProfile(Model model) {
        // Buscamos al usuario con ID 2 para la demo
        // Más adelante, aquí buscaremos al usuario que haya hecho login
        Optional<User> user = userService.findById(2L);

        if (user.isPresent()) {
            model.addAttribute("user", user.get());
            return "user-profile"; // Esto busca el archivo user-profile.html
        } else {
            return "redirect:/"; // Si no existe el usuario 2, te manda a la home
        }
    }

    @GetMapping("/edit-profile")
    public String editProfile(Model model) {
        // Buscamos al usuario 2 para la demo (luego será el de la sesión)
        Optional<User> user = userService.findById(2L);
        if (user.isPresent()) {
            model.addAttribute("user", user.get());
            return "edit-profile"; // Carga el template edit-profile.html
        }
        return "redirect:/user-profile";
    }

    @PostMapping("/edit-profile/save")
    public String saveProfile(
            @RequestParam Long id,
            @RequestParam String fullName,
            @RequestParam String userEmail,
            @RequestParam("userAvatar") MultipartFile imageFile) throws IOException {

        User user = userService.findById(id).orElseThrow();
        user.setName(fullName);
        user.setEmail(userEmail);

        if (!imageFile.isEmpty()) {
            user.setImage(imageFile.getBytes());
        }

        userService.save(user); // Hidden ID field in the form ensures we update the existing user instead of creating a new one
        return "redirect:/user-profile";
    }
}
