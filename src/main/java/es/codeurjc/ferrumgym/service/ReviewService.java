package es.codeurjc.ferrumgym.service;

import es.codeurjc.ferrumgym.dto.ReviewDTO;
import es.codeurjc.ferrumgym.model.Activity;
import es.codeurjc.ferrumgym.model.User;
import es.codeurjc.ferrumgym.repository.*;

import es.codeurjc.ferrumgym.model.Review;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReviewService {

    @Autowired
    private UserRepository userRepository; // ¡Esta línea te falta!

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
        // 1. Buscamos las entidades relacionadas
        User user = userRepository.findById(reviewDto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Activity activity = activityRepository.findById(reviewDto.getActivityId())
                .orElseThrow(() -> new RuntimeException("Activity not found"));

        // 2. Creamos la entidad Review con los datos del DTO
        Review review = new Review();
        review.setComment(reviewDto.getComment());
        review.setRating(reviewDto.getRating());
        review.setUser(user);
        review.setActivity(activity);

        // 3. Guardamos y devolvemos el DTO
        Review savedReview = reviewRepository.save(review);
        return new ReviewDTO(savedReview);
    }

    public void deleteById(Long id) {
        reviewRepository.deleteById(id);
    }
}