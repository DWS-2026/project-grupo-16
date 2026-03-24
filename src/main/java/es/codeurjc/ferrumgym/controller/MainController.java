package es.codeurjc.ferrumgym.controller;

import es.codeurjc.ferrumgym.model.Activity;
import es.codeurjc.ferrumgym.model.Booking;
import es.codeurjc.ferrumgym.model.Review;
import es.codeurjc.ferrumgym.model.User;
import es.codeurjc.ferrumgym.service.ActivityService;
import es.codeurjc.ferrumgym.service.BookingService;
import es.codeurjc.ferrumgym.service.ReviewService;
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
import java.security.Principal;
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

    @Autowired
    private es.codeurjc.ferrumgym.repository.BookingRepository bookingRepository;

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
        // Al no haber base de datos, Spring simplemente busca 
        // el archivo "prices.html" en la carpeta templates y lo lanza.
        return "prices";
    }

    @GetMapping("/activity/{id}")
    public String activityDetail(Model model, @PathVariable long id, @RequestParam(required = false) String error) {
        Optional<Activity> activity = activityService.findById(id);
        if (activity.isPresent()) {
            model.addAttribute("activity", activity.get());

            // ¡NUEVO! Si viene con el error de duplicado por la URL, preparamos el mensaje
            if ("already_booked".equals(error)) {
                model.addAttribute("errorMessage", "You are already enrolled in this class!");
            }

            return "activity-detail";
        } else {
            return "404";
        }
    }

    // POST Method to handle the "Add Review" form submission
    @PostMapping("/activity/{id}/review")
    public String addReview(@PathVariable long id, @RequestParam String comment, @RequestParam int rating, Principal principal) {
        Optional<Activity> activity = activityService.findById(id);

        if (activity.isPresent()) {
            Review review = new Review();
            review.setComment(comment);
            review.setRating(rating);
            review.setActivity(activity.get());

            // 1. Sacamos el email del usuario de la sesión actual
            String email = principal.getName();
            
            // 2. Buscamos a ESE usuario en la base de datos
            User currentUser = userService.findByEmail(email).orElseThrow();
            
            // 3. Le asignamos la reseña al autor real
            review.setUser(currentUser);

            reviewService.save(review);
        }

        // Redirige de vuelta a la página de detalles
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

    @GetMapping("/review/{id}/image")
    public ResponseEntity<Object> downloadReviewImage(@PathVariable long id) {
        Optional<Review> review = reviewService.findById(id);
        if (review.isPresent() && review.get().getHasImage() && review.get().getImageFile() != null) {
            return ResponseEntity.ok()
                    .header(org.springframework.http.HttpHeaders.CONTENT_TYPE, "image/jpeg")
                    .body(review.get().getImageFile());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Método POST para procesar el botón de "Book Class"
    @PostMapping("/activity/{id}/book")
    public String bookClass(@PathVariable Long id, Principal principal, Model model) {
        // 1. Buscamos la actividad
        Activity activity = activityService.findById(id).orElseThrow();
        
        // 2. Buscamos al usuario logueado
        String email = principal.getName();
        User currentUser = userService.findByEmail(email).orElseThrow();

        // 3. Comprobamos si el usuario ya tiene un "Booking" para esta actividad
        boolean isEnrolled = false;
        for (Booking b : activity.getBookings()) {
            if (b.getUser().getId().equals(currentUser.getId())) {
                isEnrolled = true;
                break;
            }
        }

        if (isEnrolled) {
            model.addAttribute("alreadyEnrolled", true);
        } else {
            // 4. Si hay hueco, creamos una reserva nueva SOLO con usuario y actividad
            if (activity.getEnrolled() < activity.getCapacity()) {
                Booking newBooking = new Booking();
                newBooking.setUser(currentUser);
                newBooking.setActivity(activity);
                
                // 5. Guardamos la reserva en la base de datos
                bookingRepository.save(newBooking); 
                
                // 6. Sumamos 1 al número de apuntados y actualizamos la actividad
                activity.setEnrolled(activity.getEnrolled() + 1);
                activityService.save(activity);
                
                model.addAttribute("enrollSuccess", true);
            } else {
                model.addAttribute("fullClass", true); 
            }
        }
        
        // Volvemos a pasar la actividad para que la página se vea bien
        model.addAttribute("activity", activity);
        return "activity-detail";
    }

    //Gestion de imagenes de usuario
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
        // 1. Sacamos el email del usuario que ha iniciado sesión
        String email = request.getUserPrincipal().getName();
        
        // 2. Buscamos a ESE usuario en la base de datos
        User currentUser = userService.findByEmail(email).orElseThrow();
        
        // 3. Pasamos los datos del usuario a la vista
        model.addAttribute("user", currentUser);
        
        // 4. Pasamos SUS reservas a la vista
        // Como tu entidad User ya tiene una lista de Bookings, es así de fácil:
        List<Booking> myBookings = currentUser.getBookings();
        model.addAttribute("bookings", myBookings);
        model.addAttribute("bookingCount", myBookings.size());
        
        return "user-profile";
    }

    // 1. Mostrar la página de editar perfil con TUS datos
    @GetMapping("/edit-profile")
    public String editProfile(Model model, Principal principal) {
        // Sacamos tu email de la sesión
        String email = principal.getName();
        // Buscamos tu usuario en la base de datos
        User currentUser = userService.findByEmail(email).orElseThrow();
        
        // Pasamos TU usuario a la vista
        model.addAttribute("user", currentUser);
        return "edit-profile"; 
    }

    // 2. Guardar los cambios en TU perfil
    @PostMapping("/edit-profile/save")
    public String saveProfile(
            @RequestParam String fullName,
            @RequestParam String userEmail,
            @RequestParam("userAvatar") MultipartFile imageFile,
            Principal principal) throws IOException { // Añadimos Principal aquí también

        // Sacamos quién eres a través de la sesión de Spring Security
        String currentEmail = principal.getName();
        User currentUser = userService.findByEmail(currentEmail).orElseThrow();

        // Actualizamos TUS datos
        currentUser.setName(fullName);
        currentUser.setEmail(userEmail);

        // Si has subido una foto nueva, la guardamos
        if (!imageFile.isEmpty()) {
            currentUser.setImage(imageFile.getBytes());
        }

        userService.save(currentUser); 
        return "redirect:/user-profile";
    }
    
    @GetMapping("/booking/cancel/{id}")
    public String cancelBooking(@PathVariable Long id) {
        // 1. Buscamos la reserva para saber qué actividad era
        Booking booking = bookingService.findById(id).orElseThrow();
        Activity activity = booking.getActivity();

        // 2. Restamos uno al contador de la actividad
        activity.setEnrolled(activity.getEnrolled() - 1);
        activityService.save(activity);

        // 3. Borramos la reserva de MySQL
        bookingService.deleteById(id);

        return "redirect:/user-profile";
    }

    @GetMapping("/login")
    public String login() {
        return "login"; // Carga login.html
    }

    @GetMapping("/register")
    public String register() {
        return "register"; // Carga register.html
    }

   //Controlador de registros de usuario
   @PostMapping("/register")
    public String registerUser(@RequestParam String name, @RequestParam String email, @RequestParam String password,
            @RequestParam("formFile") MultipartFile imageFile) throws IOException {

        // Encriptamos la contraseña antes de guardarla
        String encodedPassword = passwordEncoder.encode(password);

        // Usamos la contraseña encriptada al crear el usuario
        User newUser = new User(name, email, encodedPassword, List.of("ROLE_USER"));

        // Guardamos la foto si la ha subido
        if (!imageFile.isEmpty()) {
            newUser.setImage(imageFile.getBytes());
        }

        userService.save(newUser);
        return "redirect:/login";
    }

    @GetMapping("/forgot-password")
    public String forgotPassword() {
        return "forgot-password"; // Carga forgot-password.html
    }
    
    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam String email, Model model) {
        // 1. Aquí (en el futuro) buscarías si el email existe en tu userService
        // User user = userService.findByEmail(email);
        
        // 2. Aquí generarías el token y enviarías el email real.
        
        // 3. Por ahora, simulamos el éxito y devolvemos el mensaje a la vista
        model.addAttribute("message", "If an account with the email " + email + " exists, instructions have been sent.");
        
        return "forgot-password"; 
    }
}