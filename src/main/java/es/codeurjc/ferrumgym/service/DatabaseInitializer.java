package es.codeurjc.ferrumgym.service;

import es.codeurjc.ferrumgym.model.*;
import es.codeurjc.ferrumgym.repository.*;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class DatabaseInitializer {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @PostConstruct
    public void init() {
        // Only initialize if the database is empty to avoid overwriting existing data
        if (userRepository.count() == 0) {

            System.out.println("⏳ Cargando datos de prueba en la Base de Datos...");

            // Adding users
            User admin = new User();
            admin.setName("Admin Principal");
            admin.setEmail("admin@ferrumgym.com");
            admin.setEncodedPassword("pass123"); // hardcoded for simplicity
            admin.setRoles(List.of("ROLE_USER", "ROLE_ADMIN"));
            userRepository.save(admin);

            User client = new User();
            client.setName("Juan Pérez");
            client.setEmail("juan@ferrumgym.com");
            client.setEncodedPassword("pass123");
            client.setRoles(List.of("ROLE_USER"));
            userRepository.save(client);

            // New Activities
            Activity crossfit = new Activity();
            crossfit.setName("Crossfit Nivel 1");
            crossfit.setDescription("Clase de alta intensidad para empezar el día con fuerza.");
            activityRepository.save(crossfit);

            Activity yoga = new Activity();
            yoga.setName("Yoga Relax");
            yoga.setDescription("Estiramientos y relajación para terminar el día.");
            activityRepository.save(yoga);

            // Bookings
            Booking booking = new Booking();
            booking.setUser(client);
            booking.setActivity(crossfit);
            booking.setBookingDate(LocalDateTime.now().plusDays(2)); // Book for 2 days later
            bookingRepository.save(booking);

            // Reviews
            Review review = new Review();
            review.setComment("¡Me encantó la clase de Crossfit! El monitor es genial.");
            review.setRating(5);
            review.setUser(client);
            review.setActivity(crossfit);
            reviewRepository.save(review);
        }
    }
}
