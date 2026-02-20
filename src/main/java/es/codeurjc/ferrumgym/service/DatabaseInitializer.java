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

            System.out.println("⏳ Loading mock data into the database...");

            // 1. ADDING USERS
            User admin = new User();
            admin.setName("Main Admin");
            admin.setEmail("admin@ferrumgym.com");
            admin.setEncodedPassword("pass123"); // Hardcoded for simplicity right now
            admin.setRoles(List.of("ROLE_USER", "ROLE_ADMIN"));
            userRepository.save(admin);

            User client = new User();
            client.setName("John Doe");
            client.setEmail("john@ferrumgym.com");
            client.setEncodedPassword("pass123");
            client.setRoles(List.of("ROLE_USER"));
            userRepository.save(client);

            // 2. ADDING ACTIVITIES
            Activity crossfit = new Activity();
            crossfit.setName("Crossfit Level 1");
            crossfit.setDescription("High-intensity class to start your day with energy.");
            crossfit.setTrainer("Alicia Garcia");
            crossfit.setSchedule("Mon-Wed 17:00-18:00");
            crossfit.setCapacity(25);
            crossfit.setEnrolled(15);
            activityRepository.save(crossfit);

            Activity yoga = new Activity();
            yoga.setName("Yoga Relax");
            yoga.setDescription("Stretching and relaxation to end your day.");
            yoga.setTrainer("Mark Perez");
            yoga.setSchedule("Tue-Thu 19:00-20:00");
            yoga.setCapacity(20);
            yoga.setEnrolled(20);
            activityRepository.save(yoga);

            // 3. ADDING BOOKINGS
            Booking booking = new Booking();
            booking.setUser(client);
            booking.setActivity(crossfit);
            booking.setBookingDate(LocalDateTime.now().plusDays(2)); // Booked for 2 days from now
            bookingRepository.save(booking);

            // 4. ADDING REVIEWS
            Review review = new Review();
            review.setComment("I loved the Crossfit class! The trainer is awesome.");
            review.setRating(5);
            review.setUser(client);
            review.setActivity(crossfit);
            reviewRepository.save(review);
        }
    }
}
