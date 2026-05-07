package es.codeurjc.ferrumgym.controller.rest;

import es.codeurjc.ferrumgym.dto.BookingDTO;
import es.codeurjc.ferrumgym.dto.BookingMapper;
import es.codeurjc.ferrumgym.model.Booking;
import es.codeurjc.ferrumgym.service.BookingService;
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
@RequestMapping("/api/v1/bookings")
public class BookingRestController {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private BookingMapper bookingMapper;

    @Operation(summary = "Get all bookings paginated")
    @GetMapping
    public ResponseEntity<Page<BookingDTO>> getAllBookings(@PageableDefault(size = 10) Pageable pageable) {
        // Paginated endpoint to get all bookings, mapping Entities to DTOs for safe output
        Page<Booking> bookings = bookingService.findAll(pageable);
        return ResponseEntity.ok(bookings.map(bookingMapper::toDTO));
    }

    // ... (Other endpoints remain unchanged)

    @Operation(summary = "Cancel/Delete a booking")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Booking cancelled successfully"),
        @ApiResponse(responseCode = "403", description = "Forbidden: Not the owner or admin"),
        @ApiResponse(responseCode = "404", description = "Booking not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelBooking(@PathVariable Long id) {
        // The service layer handles security logic (IDOR protection) and physical deletion
        bookingService.deleteById(id);
        
        // Returns 204 No Content indicating success without an explicit body
        return ResponseEntity.noContent().build();
    }
}