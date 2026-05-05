package es.codeurjc.ferrumgym.controller.rest;

import es.codeurjc.ferrumgym.dto.ReviewDTO;
import es.codeurjc.ferrumgym.dto.ReviewMapper;   
import es.codeurjc.ferrumgym.model.Review;
import es.codeurjc.ferrumgym.service.ReviewService;
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

@RestController
@RequestMapping("/api/v1/reviews")
public class ReviewRestController {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private ReviewMapper reviewMapper;

    @Operation(summary = "Get all reviews paginated")
    @GetMapping
    public ResponseEntity<Page<ReviewDTO>> getAllReviews(@PageableDefault(size = 10) Pageable pageable) {
        Page<Review> reviews = reviewService.findAll(pageable);
        // Transformación de Entidad a Record en la salida
        return ResponseEntity.ok(reviews.map(reviewMapper::toDTO));
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

    /**
     * Nuevo: Método para borrar reseñas desde la API
     * Aprovecha la lógica de seguridad (Dueño/Admin) que escribimos en el Service.
     */
    @Operation(summary = "Delete a review (Owner or Admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Review deleted"),
        @ApiResponse(responseCode = "403", description = "Forbidden: Not the owner or admin"),
        @ApiResponse(responseCode = "404", description = "Review not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long id) {
        reviewService.deleteById(id); // El servicio lanza 403 o 404 si corresponde
        return ResponseEntity.noContent().build();
    }
}