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
    public ResponseEntity<Page<ReviewDTO>> getAllReviews(@PageableDefault(size = 10) Pageable pageable) {
        Page<Review> reviews = reviewService.findAll(pageable);
        // Transformation of Entity to Record on the output
        return ResponseEntity.ok(reviews.map(reviewMapper::toDTO));
    }

    @Operation(summary = "Create a new review for an activity")
    @PostMapping("/activity/{activityId}")
    public ResponseEntity<ReviewDTO> createReview(
            @PathVariable Long activityId,
            @RequestParam String comment,
            @RequestParam int rating,
            @RequestParam(required = false) MultipartFile imageFile) throws java.io.IOException {
        
        // 1. Identify the current user by the Token
        String email = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userService.findByEmail(email)
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.UNAUTHORIZED));

        // 2. Search the activity
        Activity activity = activityService.findById(activityId)
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Activity not found"));

        // 3. Create the entity Review
        Review review = new Review();
        review.setComment(comment);
        review.setRating(rating);
        review.setUser(currentUser);
        review.setActivity(activity);

        // 4. Manage the image if it exists
        if (imageFile != null && !imageFile.isEmpty()) {
            review.setImageFile(imageFile.getBytes());
            review.setHasImage(true);
        }

        Review savedReview = reviewService.save(review);
        return ResponseEntity.status(org.springframework.http.HttpStatus.CREATED)
                .body(reviewMapper.toDTO(savedReview));
    }

    @Operation(summary = "Get a review by its id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Review found",
            content = { @Content(mediaType = "application/json",
            schema = @Schema(implementation = ReviewDTO.class)) }), 
        @ApiResponse(responseCode = "404", description = "Review not found", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<ReviewDTO> getReviewById(@PathVariable Long id) {
        return reviewService.findById(id)
                .map(review -> ResponseEntity.ok(reviewMapper.toDTO(review)))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @Operation(summary = "Get the image attached to a review")
    @GetMapping("/{id}/image")
    public ResponseEntity<byte[]> getReviewImage(@PathVariable Long id) {
        return reviewService.findById(id)
                .filter(review -> review.getImageFile() != null) 
                .map(review -> ResponseEntity.ok()
                        .header("Content-Type", "image/jpeg")
                        .body(review.getImageFile())) 
                .orElse(ResponseEntity.notFound().build()); 
    }

    @Operation(summary = "Delete a review (Owner or Admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Review deleted"),
        @ApiResponse(responseCode = "403", description = "Forbidden: Not the owner or admin"),
        @ApiResponse(responseCode = "404", description = "Review not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long id) {
        reviewService.deleteById(id); // The service returns a 403 or 404 error if applicable.
        return ResponseEntity.noContent().build();
    }
}