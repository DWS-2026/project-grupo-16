package es.codeurjc.ferrumgym.service;

import es.codeurjc.ferrumgym.model.Tariff;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Arrays;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder; // RECUPERADO
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

    @Autowired
    private SiteSettingsRepository siteSettingsRepository;
    
    @Autowired
    private TariffService tariffService;

    @Autowired
    private PasswordEncoder passwordEncoder; // RECUPERADO

    @PostConstruct
    public void init() throws IOException {

        if (userRepository.count() == 0) {

            Activity yoga = new Activity();
            yoga.setName("Yoga");
            yoga.setDescription("Relaxing sessions for mind and body.");
            yoga.setImage(loadImage("src/main/resources/static/assets/Yoga.jpg"));
            yoga.setTrainer("Mark Perez");
            yoga.setSchedule("Tue-Thu 19:00-20:00");
            yoga.setCapacity(20);
            yoga.setEnrolled(20);
            yoga.setPdfFilename("Yoga.pdf");
            activityRepository.save(yoga);

            Activity crossfit = new Activity();
            crossfit.setName("Crossfit");
            crossfit.setDescription("High intensity interval training.");
            crossfit.setImage(loadImage("src/main/resources/static/assets/crossfit.avif"));
            crossfit.setTrainer("Alicia Garcia");
            crossfit.setSchedule("Mon-Wed 17:00-18:00");
            crossfit.setCapacity(25);
            crossfit.setEnrolled(15);
            crossfit.setPdfFilename("Crossfit.pdf");
            activityRepository.save(crossfit);

            Activity zumba = new Activity();
            zumba.setName("Zumba");
            zumba.setDescription("Dance and fitness to high-energy music.");
            zumba.setImage(loadImage("src/main/resources/static/assets/Zumba.webp"));
            zumba.setTrainer("Marta Ruiz");
            zumba.setSchedule("Mon-Wed 20:00-21:00");
            zumba.setCapacity(30);
            zumba.setEnrolled(10);
            zumba.setPdfFilename("Zumba.pdf");
            activityRepository.save(zumba);

            Activity spinning = new Activity();
            spinning.setName("Spinning");
            spinning.setDescription("Intense indoor cycling workout.");
            spinning.setImage(loadImage("src/main/resources/static/assets/spinning.jpg"));
            spinning.setTrainer("Roberto Soler");
            spinning.setSchedule("Fri 18:00-19:00");
            spinning.setCapacity(15);
            spinning.setEnrolled(12);
            spinning.setPdfFilename("Spinning.pdf");
            activityRepository.save(spinning);

            // Usuarios con contraseñas encriptadas
            User admin = new User();
            admin.setName("Admin");
            admin.setEmail("admin@ferrumgym.com");
            admin.setEncodedPassword(passwordEncoder.encode("adminpass")); // RECUPERADO
            admin.setRoles(Arrays.asList("ROLE_USER", "ROLE_ADMIN"));
            admin.setImage(loadImage("src/main/resources/static/assets/foto.avif"));
            userRepository.save(admin);

            User user1 = new User();
            user1.setName("Juan Perez");
            user1.setEmail("j.perez@alumnos.urjc.es");
            user1.setEncodedPassword(passwordEncoder.encode("pass1")); // RECUPERADO
            user1.setRoles(Arrays.asList("ROLE_USER"));
            user1.setImage(loadImage("src/main/resources/static/assets/foto.avif"));
            userRepository.save(user1);

            User user2 = new User();
            user2.setName("Marta Gomez");
            user2.setEmail("m.gomez@alumnos.urjc.es");
            user2.setEncodedPassword(passwordEncoder.encode("pass2")); // RECUPERADO
            user2.setRoles(Arrays.asList("ROLE_USER"));
            user2.setImage(loadImage("src/main/resources/static/assets/foto.avif"));
            userRepository.save(user2);

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
            review.setHasImage(false);
            reviewRepository.save(review);

            SiteSettings settings = new SiteSettings();
            settings.setGymName("Ferrum Gym");
            settings.setContactEmail("info@ferrumgym.com");
            settings.setContactPhone("+34 912 345 678");
            settings.setAddress("Calle Tulipán s/n. 28933 Móstoles (Madrid)");
            settings.setWeekdaysHours("07:00 - 23:00");
            settings.setWeekendsHours("09:00 - 21:00");
            siteSettingsRepository.save(settings);
        }
        
        if (tariffService.findAll().isEmpty()) {
            Tariff basic = new Tariff("Iron Basic (Gym Only)", 30.0, "/mo", "Unlimited Gym Access. Free Weights & Machines area.");
            Tariff hybrid = new Tariff("Standard Hybrid", 45.0, "/mo", "Gym Access + Classes. Up to 2 Classes / Week.");
            Tariff pro = new Tariff("Ultimate Pro", 65.0, "/mo", "Unlimited Everything. Gym + Unlimited Classes.");
            Tariff voucher = new Tariff("Class Voucher (10)", 50.0, "/pack", "10 Group Classes. Valid for 6 months (Yoga, CrossFit, Zumba...).");
            Tariff dayPass = new Tariff("Day Pass", 12.0, "/day", "1 Day Full Access. Gym + 1 Class included. No commitment.");

            tariffService.save(basic);
            tariffService.save(hybrid);
            tariffService.save(pro);
            tariffService.save(voucher);
            tariffService.save(dayPass);
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