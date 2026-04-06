package es.codeurjc.ferrumgym.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class Activity {

    public Activity() {}

    public Activity(Long id, String name, String description, byte[] image, String trainer, String schedule, int capacity, List<Booking> bookings) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.image = image;
        this.trainer = trainer;
        this.schedule = schedule;
        this.capacity = capacity;
        this.bookings = bookings;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] image;

    private String trainer;
    private String schedule;
    private int capacity;
    private int enrolled;
    
    private String pdfFilename;

    @OneToMany(mappedBy = "activity", cascade = CascadeType.ALL)
    private List<Booking> bookings;

    @OneToMany(mappedBy = "activity", cascade = CascadeType.ALL)
    private List<Review> reviews;

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
        } else if (this.enrolled >= this.capacity - 5) {
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