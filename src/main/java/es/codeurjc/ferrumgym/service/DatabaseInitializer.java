package es.codeurjc.ferrumgym;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Arrays;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.codeurjc.ferrumgym.model.*;
import es.codeurjc.ferrumgym.repository.*;

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
    public void init() throws IOException {

        // 1. SAFETY CHECK: Only run if DB is empty to prevent duplicates on restart
        if (userRepository.count() == 0) {

            System.out.println("⏳ Loading mock data with images into the database...");

            // 2. Create Activities (Using setters to include the Dashboard fields)
            Activity yoga = new Activity();
            yoga.setName("Yoga");
            yoga.setDescription("Relaxing sessions for mind and body.");
            yoga.setImage(loadImage("src/main/resources/static/assets/Yoga.jpg"));
            yoga.setTrainer("Mark Perez");
            yoga.setSchedule("Tue-Thu 19:00-20:00");
            yoga.setCapacity(20);
            yoga.setEnrolled(20);
            activityRepository.save(yoga);

            Activity crossfit = new Activity();
            crossfit.setName("Crossfit");
            crossfit.setDescription("High intensity interval training.");
            crossfit.setImage(loadImage("src/main/resources/static/assets/crossfit.avif"));
            crossfit.setTrainer("Alicia Garcia");
            crossfit.setSchedule("Mon-Wed 17:00-18:00");
            crossfit.setCapacity(25);
            crossfit.setEnrolled(15);
            activityRepository.save(crossfit);

            // 3. Create Administrator
            User admin = new User();
            admin.setName("Admin");
            admin.setEmail("admin@ferrumgym.com");
            admin.setEncodedPassword("adminpass");
            admin.setRoles(Arrays.asList("ROLE_USER", "ROLE_ADMIN"));
            admin.setImage(loadImage("src/main/resources/static/assets/foto.avif"));
            userRepository.save(admin);

            // 4. Create Registered Users
            User user1 = new User();
            user1.setName("Juan Perez");
            user1.setEmail("j.perez@alumnos.urjc.es");
            user1.setEncodedPassword("pass1");
            user1.setRoles(Arrays.asList("ROLE_USER"));
            user1.setImage(loadImage("src/main/resources/static/assets/foto.avif"));
            userRepository.save(user1);

            User user2 = new User();
            user2.setName("Marta Gomez");
            user2.setEmail("m.gomez@alumnos.urjc.es");
            user2.setEncodedPassword("pass2");
            user2.setRoles(Arrays.asList("ROLE_USER"));
            user2.setImage(loadImage("src/main/resources/static/assets/foto.avif"));
            userRepository.save(user2);

            // 5. Create Mock Bookings & Reviews for the Dashboard
            Booking booking = new Booking();
            booking.setUser(user1);
            booking.setActivity(crossfit);
            booking.setBookingDate(LocalDateTime.now().plusDays(2));
            bookingRepository.save(booking);

            Review review = new Review();
            review.setComment("I loved the Crossfit class! The trainer is awesome.");
            review.setRating(5);
            review.setUser(user1);
            review.setActivity(crossfit);
            reviewRepository.save(review);
        }
    }

    private byte[] loadImage(String path) throws IOException {
        Path imagePath = Paths.get(path);
        if (Files.exists(imagePath)) {
            return Files.readAllBytes(imagePath);
        }
        return null;
    }
}
