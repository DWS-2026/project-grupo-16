package es.codeurjc.ferrumgym.service;

import es.codeurjc.ferrumgym.dto.BookingDTO;
import es.codeurjc.ferrumgym.model.Activity;
import es.codeurjc.ferrumgym.model.Booking;
import es.codeurjc.ferrumgym.repository.*;

import org.springframework.beans.factory.annotation.Autowired;
import es.codeurjc.ferrumgym.model.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository; 

    @Autowired
    private ActivityRepository activityRepository;

    public List<Booking> findAll() {
        return bookingRepository.findAll();
    }

    public List<Booking> findByActivityId(Long activityId) {
        return bookingRepository.findByActivityId(activityId);
    }

    public Optional<Booking> findById(Long id) {
        return bookingRepository.findById(id);
    }

    public BookingDTO save(BookingDTO bookingDto) {
        // 1. Buscamos las entidades relacionadas (User y Activity)
        User user = userRepository.findById(bookingDto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Activity activity = activityRepository.findById(bookingDto.getActivityId())
                .orElseThrow(() -> new RuntimeException("Activity not found"));

        // 2. Creamos la entidad Booking
        Booking booking = new Booking();
        booking.setUser(user);
        booking.setActivity(activity);

        // 3. Guardamos y devolvemos el DTO
        Booking savedBooking = bookingRepository.save(booking);
        return new BookingDTO(savedBooking);
    }

    public void deleteById(Long id) {
        bookingRepository.deleteById(id);
    }

    public boolean existsByUserAndActivity(User user, Activity activity) {
        return bookingRepository.existsByUserAndActivity(user, activity);
    }
}
