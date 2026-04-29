package es.codeurjc.ferrumgym.service;

import es.codeurjc.ferrumgym.dto.ReviewDTO;
import es.codeurjc.ferrumgym.model.Activity;
import es.codeurjc.ferrumgym.model.User;
import es.codeurjc.ferrumgym.repository.*;

import es.codeurjc.ferrumgym.model.Review;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class ReviewService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    public List<Review> findAll() {
        return reviewRepository.findAll();
    }

    public Optional<Review> findById(Long id) {
        return reviewRepository.findById(id);
    }

    public ReviewDTO save(ReviewDTO reviewDto) {
        User user = userRepository.findById(reviewDto.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        
        Activity activity = activityRepository.findById(reviewDto.getActivityId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Actividad no encontrada"));

        Review review = new Review();
        review.setComment(reviewDto.getComment());
        review.setRating(reviewDto.getRating());
        review.setUser(user);
        review.setActivity(activity);
        
        // Si la reseña tiene imagen, aquí deberías gestionar los bytes si los incluyes en el DTO

        Review savedReview = reviewRepository.save(review);
        return new ReviewDTO(savedReview);
    }

    public void deleteById(Long id) {
        // 1. Buscamos la reseña
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reseña no encontrada"));

        // 2. Identificamos al usuario actual
        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        // 3. Verificamos si es el dueño O administrador
        if (review.getUser().equals(currentUser) || currentUser.getRoles().contains("ADMIN")) {
            reviewRepository.deleteById(id);
        } else {
            // Error 403 en formato JSON para la API
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permiso para borrar esta reseña");
        }
    }
}