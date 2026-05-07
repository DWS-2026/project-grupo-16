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
        Page<Booking> bookings = bookingService.findAll(pageable);
        // El Mapper transforma la Entidad en el Record de respuesta
        return ResponseEntity.ok(bookings.map(bookingMapper::toDTO));
    }

    @Operation(summary = "Create a new booking with specific date and time")
    @PostMapping
    public ResponseEntity<BookingDTO> createBooking(@RequestBody java.util.Map<String, Object> request) {
        // 1. Extraemos los datos del JSON de Postman
        Long activityId = Long.valueOf(request.get("activityId").toString());
        String bookingDateStr = (String) request.get("bookingDate"); // Ejemplo: "2026-05-12T19:00:00"

        // 2. Llamamos al servicio para validar y guardar
        Booking newBooking = bookingService.save(activityId, bookingDateStr);

        // 3. Devolvemos el DTO de la reserva creada
        return ResponseEntity.status(org.springframework.http.HttpStatus.CREATED)
                .body(bookingMapper.toDTO(newBooking));
    }

    @Operation(summary = "Get a booking by its id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Booking found",
            content = { @Content(mediaType = "application/json",
            schema = @Schema(implementation = BookingDTO.class)) }), 
        @ApiResponse(responseCode = "404", description = "Booking not found", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<BookingDTO> getBookingById(@PathVariable Long id) {
        return bookingService.findById(id)
                .map(booking -> ResponseEntity.ok(bookingMapper.toDTO(booking)))
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Cancel/Delete a booking")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Booking cancelled successfully"),
        @ApiResponse(responseCode = "403", description = "Forbidden: Not the owner or admin"),
        @ApiResponse(responseCode = "404", description = "Booking not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelBooking(@PathVariable Long id) {
        // El servicio gestiona la lógica de seguridad y el borrado físico
        bookingService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}