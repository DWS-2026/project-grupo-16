package es.codeurjc.ferrumgym.controller;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
public class AdminController {


    // --- CLASES DE DATOS (MOCK) ---
    // Usamos 'record' para crear objetos de datos rápidos (Java 14+)

    // 1. Actividad (Clase)
    public record Activity(long id, String name, String trainer, String schedule, int enrolled, int capacity) {
        public boolean isFull() { return enrolled >= capacity; }
        public String statusColor() {
            if (isFull()) return "bg-danger"; // Rojo si está lleno
            if (enrolled >= capacity - 5) return "bg-warning"; // Amarillo si quedan pocas
            return "bg-success"; // Verde si hay sitio
        }
    }

    // 2. Usuario
    public record User(long id, String name, String email, String role, boolean hasImage) {}

    // 3. Reseña
    public record Review(long id, String className, int stars, String comment, String author, boolean hasImage) {
        public String starsDisplay() { return "★".repeat(stars); } // Crea string de estrellitas
    }

    @GetMapping("/admin-dashboard")
    public String dashboard(Model model) {
        // --- CARGA DE DATOS MOCK ---

        // Lista de Actividades
        List<Activity> activities = new ArrayList<>();
        activities.add(new Activity(1, "Crossfit", "Trainer 1", "Mon & Wed 17:00-18:00", 15, 25));
        activities.add(new Activity(2, "Body Pump", "Trainer 2", "Mon & Wed 18:00-19:00", 18, 30));
        activities.add(new Activity(3, "Yoga", "Trainer 3", "Mon & Wed 19:00-20:00", 20, 20)); // Llena
        activities.add(new Activity(4, "Spinning", "Trainer 4", "Mon & Wed 20:00-21:00", 22, 25));
        activities.add(new Activity(5, "HIIT", "Trainer 5", "Tue & Thu 17:00-18:00", 14, 20));
        activities.add(new Activity(6, "Pilates", "Trainer 6", "Tue & Thu 18:00-19:00", 16, 28));
        activities.add(new Activity(7, "Boxing", "Trainer 7", "Tue & Thu 19:00-20:00", 24, 30));
        activities.add(new Activity(8, "Zumba", "Trainer 8", "Tue & Thu 20:00-21:00", 19, 32));

        // Lista de Usuarios
        List<User> users = new ArrayList<>();
        users.add(new User(1, "Juan Pérez", "juan@alumnos.urjc.es", "Member", true));
        users.add(new User(2, "Alicia García", "alicia@alumnos.urjc.es", "Trainer", true));

        // Lista de Reseñas
        List<Review> reviews = new ArrayList<>();
        reviews.add(new Review(1, "HIIT Class", 4, "Great class but a bit crowded, they could run more groups. See you next time!", "Nombre Apellido", true));
        reviews.add(new Review(2, "Crossfit Class", 5, "It was the best class of my life. Thank you!", "Nombre Apellido", true));

        // --- PASAR AL MODELO ---
        model.addAttribute("activities", activities);
        model.addAttribute("users", users);
        model.addAttribute("reviews", reviews);
        model.addAttribute("adminName", "Admin"); // Para el navbar

        return "admin-dashboard";
    }
}
