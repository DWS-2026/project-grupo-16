package es.codeurjc.ferrumgym.model;

import jakarta.persistence.*;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Activity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    // HEMOS CAMBIADO ESTO: De byte[] a String
    private String imageFilename; 
    private String pdfFilename;

    private String trainer;
    private String schedule;
    private int capacity;

    @OneToMany(mappedBy = "activity", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Booking> bookings;

    @OneToMany(mappedBy = "activity", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Review> reviews;

    // Constructor vacío (Obligatorio para JPA)
    public Activity() {}

    // Constructor actualizado (Sin los bytes de imagen)
    public Activity(Long id, String name, String description, String imageFilename, String pdfFilename, String trainer, String schedule, int capacity) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.imageFilename = imageFilename;
        this.pdfFilename = pdfFilename;
        this.trainer = trainer;
        this.schedule = schedule;
        this.capacity = capacity;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImageFilename() { return imageFilename; }
    public void setImageFilename(String image) { this.imageFilename = image; }

    public String getTrainer() { return trainer; }
    public void setTrainer(String trainer) { this.trainer = trainer; }

    public String getSchedule() { return schedule; }
    public void setSchedule(String schedule) { this.schedule = schedule; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    public int getEnrolled() { return (this.bookings != null) ? this.bookings.size() : 0; }


    public List<Booking> getBookings() { return bookings; }
    public void setBookings(List<Booking> bookings) { this.bookings = bookings; }

    public List<Review> getReviews() { return reviews; }
    public void setReviews(List<Review> reviews) { this.reviews = reviews; }

    public String getPdfFilename() { return pdfFilename; }
    public void setPdfFilename(String pdfFilename) { this.pdfFilename = pdfFilename; }

    public String getStatusColor() {
        if (this.capacity == 0) return "bg-secondary";
        if (getEnrolled() >= this.capacity) {
            return "bg-danger";
        } else if (getEnrolled() >= this.capacity - 5) {
            return "bg-warning text-dark";
        } else {
            return "bg-success";
        }
    }

    public int getPercentage() {
        if (capacity == 0) return 0;
        return (getEnrolled() * 100) / capacity;
    }

    public boolean isFull() {
        return this.capacity > 0 && getEnrolled() >= this.capacity;
    }
}
