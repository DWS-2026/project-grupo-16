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

    public boolean existsByUserAndActivity(User user, Activity activity) {
        return bookingRepository.existsByUserAndActivity(user, activity);
    }

    // --- MÉTODOS DE PERSISTENCIA ---

    /**
     * 1. MÉTODO PARA LA WEB (No lo borres ni cambies)
     * Lo usan MainController y AdminController.
     * Recibe el objeto ya montado desde el formulario.
     */
    public Booking save(Booking booking) {
        return bookingRepository.save(booking); //
    }

    /**
     * 2. NUEVO MÉTODO PARA LA API REST
     * Lo usa BookingRestController.
     * Solo recibe el ID de la actividad y busca al usuario en la sesión.
     */
    public Booking save(Long activityId, String dateStr) {
        // 1. Buscamos al usuario y la actividad específica
        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated"));

        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Activity not found"));

        // 2. Parseamos la fecha que envías desde Postman
        java.time.LocalDateTime selectedDate = java.time.LocalDateTime.parse(dateStr);

        // 3. VALIDACIÓN DINÁMICA
        // Sacamos las iniciales del día (Mon, Tue, Wed...) y la hora (17, 19...)
        String dayAbbreviation = selectedDate.getDayOfWeek().name().substring(0, 3).toLowerCase(); // "mon", "tue"...
        String hourStr = String.format("%02d:00", selectedDate.getHour()); // "17:00", "19:00"...

        String schedule = activity.getSchedule().toLowerCase(); // Cogemos el horario de la DB

        // Comprobamos si el día Y la hora elegidos están escritos en el horario de esa
        // actividad
        if (!schedule.contains(dayAbbreviation) || !schedule.contains(hourStr)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Invalid slot. This activity is only available at: " + activity.getSchedule());
        }

        // 4. Si todo coincide, creamos la reserva
        Booking newBooking = new Booking();
        newBooking.setUser(currentUser);
        newBooking.setActivity(activity);
        newBooking.setBookingDate(selectedDate);
        newBooking.setAttended(false);

        return bookingRepository.save(newBooking);
    }

    // Borrado con protección de dueño (IDOR) y rol de ADMIN
    public void deleteById(Long id) {
        // 1. Buscamos la reserva o lanzamos 404
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reserva no encontrada"));

        // 2. Obtenemos el usuario actual de la sesión
        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        // 3. CONTROL DE DUEÑO: Solo borra si es el dueño O si es ADMIN
        if (booking.getUser().equals(currentUser) || currentUser.getRoles().contains("ADMIN")) {
            bookingRepository.deleteById(id);
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permiso para borrar esta reserva");
        }
    }
}