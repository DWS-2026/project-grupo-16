package es.codeurjc.ferrumgym.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin") // All admin routes will start with /admin
public class AdminController {

    // auxiliary record to represent a gym class in the dashboard
    public record GymClass(long id, String name, String coach, String schedule, int enrolled, int capacity) {
        // Auxiliary method to check if the class is full
        public boolean isFull() {
            return enrolled >= capacity;
        }
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {

        model.addAttribute("adminName", "Admin User"); // Simulation of an admin name

        // Mock data for gym classes - in a real application, this would come from the database ---- TO CHANGE
        List<GymClass> classes = new ArrayList<>();
        classes.add(new GymClass(1, "CrossFit", "Alex", "Monday 17:00", 25, 25)); // Full
        classes.add(new GymClass(2, "Yoga", "Maria", "Monday 19:00", 12, 20));
        classes.add(new GymClass(3, "BodyPump", "Juan", "Tuesday 10:00", 18, 20));

        // 2. Pass data to the view
        model.addAttribute("classes", classes);
        model.addAttribute("totalUsers", 1245);
        model.addAttribute("revenue", "12.5k");

        return "admin/dashboard"; // Looks for templates/admin/dashboard.html
    }
}
