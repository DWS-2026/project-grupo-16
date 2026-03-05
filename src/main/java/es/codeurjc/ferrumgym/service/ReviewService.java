package es.codeurjc.ferrumgym.service;

import es.codeurjc.ferrumgym.model.Review;
import es.codeurjc.ferrumgym.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    public List<Review> findAll() {
        return reviewRepository.findAll();
    }

    public Optional<Review> findById(Long id) {
        return reviewRepository.findById(id);
    }

    public void save(Review review) {
        reviewRepository.save(review);
    }

    public void deleteById(Long id) {
        reviewRepository.deleteById(id);
    }
}