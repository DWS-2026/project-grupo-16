package es.codeurjc.ferrumgym.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class Activity {

    public Activity() {}

    public Activity(Long id, String name, String description, byte[] image, String trainer, String schedule, int capacity, int enrolled, List<Booking> bookings) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.image = image;
        this.trainer = trainer;
        this.schedule = schedule;
        this.capacity = capacity;
        this.enrolled = enrolled;
        this.bookings = bookings;
    }
    
    public Activity(String name, String description) {
        this.name = name;
        this.description = description;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    @Column(columnDefinition = "TEXT")
    private String description; //TO CHECK

    @Lob
    private byte[] image; // Activity photo

    // Additional fields for activity management
    private String trainer;
    private String schedule;
    private int capacity;
    private int enrolled;
    // ------------------------------------------------

    @OneToMany(mappedBy = "activity", cascade = CascadeType.ALL)
    private List<Booking> bookings;

    // --- GETTERS Y SETTERS ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public byte[] getImage() { return image; }
    public void setImage(byte[] image) { this.image = image; }

    public String getTrainer() { return trainer; }
    public void setTrainer(String trainer) { this.trainer = trainer; }

    public String getSchedule() { return schedule; }
    public void setSchedule(String schedule) { this.schedule = schedule; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    public int getEnrolled() { return enrolled; }
    public void setEnrolled(int enrolled) { this.enrolled = enrolled; }

    public List<Booking> getBookings() { return bookings; }
    public void setBookings(List<Booking> bookings) { this.bookings = bookings; }

	public String getStatusColor() {
        if (this.capacity == 0) return "bg-secondary"; // Safety check
        if (this.enrolled >= this.capacity) {
            return "bg-danger"; // Full
        } else if (this.enrolled >= this.capacity - 5) {
            return "bg-warning text-dark"; // Almost full (yellow needs dark text)
        } else {
            return "bg-success"; // Plenty of space
        }
    }
}
