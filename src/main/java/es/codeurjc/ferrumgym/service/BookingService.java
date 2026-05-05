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

    // --- MÉTODOS DE PERSISTENCIA (Solo Entidades) ---

    //Guarda la reserva directamente como entidad
    public Booking save(Booking booking) {
        return bookingRepository.save(booking);
    }

    //Borrado con protección de dueño (IDOR) y rol de ADMIN
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