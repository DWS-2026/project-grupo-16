package es.codeurjc.ferrumgym.controller;

import es.codeurjc.ferrumgym.dto.ReviewDTO;
import es.codeurjc.ferrumgym.model.Activity;
import es.codeurjc.ferrumgym.model.Review;
import es.codeurjc.ferrumgym.model.User;
import es.codeurjc.ferrumgym.service.ActivityService;
import es.codeurjc.ferrumgym.service.ReviewService;
import es.codeurjc.ferrumgym.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.Optional;

@Controller
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private ActivityService activityService;

    @Autowired
    private UserService userService;

    // POST Method to handle the "Add Review" form submission
    @PostMapping("/activity/{id}/review")
    public String addReview(@PathVariable long id, @RequestParam String comment, @RequestParam int rating,
                            @RequestParam("imageFile") MultipartFile imageFile, Principal principal) throws IOException {
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

            // Si el usuario ha subido una foto, la guardamos
            if (!imageFile.isEmpty()) {
                review.setImageFile(imageFile.getBytes());
                review.setHasImage(true);
            }

            reviewService.save(new ReviewDTO(review));
        }

        // Redirige de vuelta a la página de detalles
        return "redirect:/activity/" + id;
    }

    // Delete review (Admin only)
   @GetMapping("/review/delete/{id}")
    public String deleteReview(@PathVariable Long id) {
        reviewService.deleteById(id);
        return "redirect:/admin-dashboard";
    }
    // Serve review images
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
}