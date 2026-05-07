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

    // --- MÉTODOS DE LECTURA ---

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

    // --- MÉTODOS DE PERSISTENCIA ---

    /**
     * 1. MÉTODO PARA LA WEB
     * Añadimos validación de duplicados y capacidad.
     */
    public Booking save(Booking booking) {
        // Validación 1: ¿Ya está inscrito?
        if (bookingRepository.existsByUserAndActivity(booking.getUser(), booking.getActivity())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya estás inscrito en esta actividad");
        }

        // Validación 2: ¿Hay sitio?
        if (booking.getActivity().getEnrolled() >= booking.getActivity().getCapacity()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La actividad está llena");
        }

        return bookingRepository.save(booking);
    }

    /**
     * 2. MÉTODO PARA LA API REST
     * Añadimos validación de duplicados y capacidad.
     */
    public Booking save(Long activityId, String dateStr) {
        // 1. Buscamos al usuario y la actividad
        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated"));

        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Activity not found"));

        // 2. VALIDACIÓN DE DUPLICADOS (Evita el problema de Postman)
        if (bookingRepository.existsByUserAndActivity(currentUser, activity)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya estás inscrito en esta actividad");
        }

        // 3. VALIDACIÓN DE CAPACIDAD
        if (activity.getEnrolled() >= activity.getCapacity()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La actividad está llena");
        }

        // 4. Parseamos la fecha y validamos horario (tu lógica anterior)
        java.time.LocalDateTime selectedDate = java.time.LocalDateTime.parse(dateStr);
        String dayAbbreviation = selectedDate.getDayOfWeek().name().substring(0, 3).toLowerCase();
        String hourStr = String.format("%02d:00", selectedDate.getHour());
        String schedule = activity.getSchedule().toLowerCase();

        if (!schedule.contains(dayAbbreviation) || !schedule.contains(hourStr)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Invalid slot. This activity is only available at: " + activity.getSchedule());
        }

        // 5. Si todo es correcto, guardamos
        Booking newBooking = new Booking();
        newBooking.setUser(currentUser);
        newBooking.setActivity(activity);
        newBooking.setBookingDate(selectedDate);
        newBooking.setAttended(false);

        return bookingRepository.save(newBooking);
    }

    /**
     * MÉTODO DE BORRADO
     * Corregido para que el Admin funcione (ROLE_ADMIN) y comparando IDs.
     */
    public void deleteById(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reserva no encontrada"));

        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        // COMPARACIÓN SEGURA: Usamos el ID y ROLE_ADMIN
        boolean isOwner = booking.getUser().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRoles().contains("ROLE_ADMIN");

        if (isOwner || isAdmin) {
            bookingRepository.deleteById(id);
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permiso para borrar esta reserva");
        }
    }
}