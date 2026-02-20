package es.codeurjc.ferrumgym;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.codeurjc.ferrumgym.model.*;
import es.codeurjc.ferrumgym.repository.*;

@Component
public class DataBaseInitializer {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ActivityRepository activityRepository;

    @PostConstruct
    public void init() throws IOException {
        
        // 1. Create Activities
        Activity yoga = new Activity("Yoga", "Relaxing sessions for mind and body.");
        yoga.setImage(loadImage("src/main/resources/static/assets/Yoga.jpg"));
        activityRepository.save(yoga);

        Activity crossfit = new Activity("Crossfit", "High intensity interval training.");
        crossfit.setImage(loadImage("src/main/resources/static/assets/crossfit.avif"));
        activityRepository.save(crossfit);

        // 2. Create Administrator (Plain text password)
        User admin = new User("Admin", "admin@ferrumgym.com", 
            "adminpass", Arrays.asList("ROLE_USER", "ROLE_ADMIN"));
        admin.setImage(loadImage("src/main/resources/static/assets/foto.avif"));
        userRepository.save(admin);

        // 3. Create Registered Users
        User user1 = new User("Juan Perez", "j.perez@alumnos.urjc.es", 
            "pass1", Arrays.asList("ROLE_USER"));
        user1.setImage(loadImage("src/main/resources/static/assets/foto.avif"));
        userRepository.save(user1);

        User user2 = new User("Marta Gomez", "m.gomez@alumnos.urjc.es", 
            "pass2", Arrays.asList("ROLE_USER"));
        user2.setImage(loadImage("src/main/resources/static/assets/foto.avif"));
        userRepository.save(user2);
    }

    private byte[] loadImage(String path) throws IOException {
        Path imagePath = Paths.get(path);
        if (Files.exists(imagePath)) {
            return Files.readAllBytes(imagePath);
        }
        return null;
    }
}