package es.codeurjc.ferrumgym.service;

import es.codeurjc.ferrumgym.model.Activity;
import es.codeurjc.ferrumgym.model.Booking;
import es.codeurjc.ferrumgym.repository.*;
import es.codeurjc.ferrumgym.model.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

@Service
public class BookingService {

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    // --- READ METHODS ---

    public List<Booking> findAll() {
        return bookingRepository.findAll();
    }

    public Page<Booking> findAll(Pageable pageable) {
        return bookingRepository.findAll(pageable);
    }

    public List<Booking> findByActivityId(Long activityId) {
        return bookingRepository.findByActivityId(activityId);
    }

    public Optional<Booking> findById(Long id) {
        return bookingRepository.findById(id);
    }

    // --- PERSISTENCE METHODS ---

    /**
     * 1. METHOD FOR THE WEB
     * Adds duplicate and capacity validation before saving.
     */
    public Booking save(Booking booking) {
        // Validation 1: Is the user already enrolled in this specific activity?
        if (bookingRepository.existsByUserAndActivity(booking.getUser(), booking.getActivity())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya estás inscrito en esta actividad");
        }

        // Validation 2: Is there available capacity for this activity?
        if (booking.getActivity().getEnrolled() >= booking.getActivity().getCapacity()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La actividad está llena");
        }

        return bookingRepository.save(booking);
    }

    /**
     * 2. METHOD FOR THE REST API
     * Handles data extraction directly from the Security Context and validates capacity.
     */
    public Booking save(Long activityId, String dateStr) {
        // 1. Fetch the current authenticated user and the requested activity
        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated"));

        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Activity not found"));

        // 2. DUPLICATE VALIDATION (Prevents Postman-related multiple booking issues)
        if (bookingRepository.existsByUserAndActivity(currentUser, activity)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya estás inscrito en esta actividad");
        }

        // 3. CAPACITY VALIDATION
        if (activity.getEnrolled() >= activity.getCapacity()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La actividad está llena");
        }

        // 4. Parse the date and validate the schedule (preserves previous time logic)
        java.time.LocalDateTime selectedDate = java.time.LocalDateTime.parse(dateStr);
        String dayAbbreviation = selectedDate.getDayOfWeek().name().substring(0, 3).toLowerCase();
        String hourStr = String.format("%02d:00", selectedDate.getHour());
        String schedule = activity.getSchedule().toLowerCase();

        if (!schedule.contains(dayAbbreviation) || !schedule.contains(hourStr)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Invalid slot. This activity is only available at: " + activity.getSchedule());
        }

        // 5. If everything is correct, save the new booking
        Booking newBooking = new Booking();
        newBooking.setUser(currentUser);
        newBooking.setActivity(activity);
        newBooking.setBookingDate(selectedDate);
        newBooking.setAttended(false);

        return bookingRepository.save(newBooking);
    }

    /**
     * DELETION METHOD
     * Corrected to allow Admin access (ROLE_ADMIN) and secure ID comparison (IDOR protection).
     */
    public void deleteById(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reserva no encontrada"));

        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        // SAFE COMPARISON: Ensures only the owner or an admin can delete the resource
        boolean isOwner = booking.getUser().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRoles().contains("ROLE_ADMIN");

        if (isOwner || isAdmin) {
            bookingRepository.deleteById(id);
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permiso para borrar esta reserva");
        }
    }
}