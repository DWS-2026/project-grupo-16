package es.codeurjc.ferrumgym.controller.rest;

import es.codeurjc.ferrumgym.dto.ReviewDTO;
import es.codeurjc.ferrumgym.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;


@RestController
@RequestMapping("/api/v1/reviews")
public class ReviewRestController {

    @Autowired
    private ReviewService reviewService;

    @PostMapping
    public ResponseEntity<ReviewDTO> createReview(@RequestBody ReviewDTO reviewDto) {
        ReviewDTO saved = reviewService.save(reviewDto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(saved.getId()).toUri();
        return ResponseEntity.created(location).body(saved);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long id) {
        // El servicio debe verificar que el borrador es el dueño o Admin [cite: 337]
        reviewService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/image")
    public ResponseEntity<byte[]> getReviewImage(@PathVariable Long id) {
        return reviewService.findById(id)
                .filter(review -> review.getImageFile() != null) 
            .map(review -> ResponseEntity.ok()
                    .header("Content-Type", "image/jpeg")
                    .body(review.getImageFile()))
            .orElse(ResponseEntity.notFound().build());
    }
}