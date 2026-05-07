package es.codeurjc.ferrumgym.controller.rest;

import es.codeurjc.ferrumgym.dto.ReviewDTO;
import es.codeurjc.ferrumgym.dto.ReviewMapper;
import es.codeurjc.ferrumgym.model.Activity;
import es.codeurjc.ferrumgym.model.User;
import es.codeurjc.ferrumgym.model.Review;
import es.codeurjc.ferrumgym.service.ActivityService;
import es.codeurjc.ferrumgym.service.ReviewService;
import es.codeurjc.ferrumgym.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/reviews")
public class ReviewRestController {

    @Autowired
    private UserService userService;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private ActivityService activityService;

    @Autowired
    private ReviewMapper reviewMapper;

    @Operation(summary = "Get all reviews paginated")
    @GetMapping
    public ResponseEntity<Page<ReviewDTO>> getReviews(@PageableDefault(size = 10) Pageable pageable) {
        // Paginated endpoint to retrieve all reviews, mapping them to DTOs instead of raw entities
        Page<Review> reviews = reviewService.findAll(pageable);
        // Transformación de Entidad a Record en la salida
        return ResponseEntity.ok(reviews.map(reviewMapper::toDTO));
    }

    // ... (Other endpoints remain unchanged)

    @Operation(summary = "Delete a review (Owner or Admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Review deleted"),
        @ApiResponse(responseCode = "403", description = "Forbidden: Not the owner or admin"),
        @ApiResponse(responseCode = "404", description = "Review not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long id) {
        // The service must verify that the requester is the owner of the review or an Admin
        // The service automatically throws a 403 Forbidden or 404 Not Found exception if applicable
        reviewService.deleteById(id); 
        return ResponseEntity.noContent().build();
    }
}