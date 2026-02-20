package es.codeurjc.ferrumgym.model;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Booking {
    
    public Booking(Long id, User user, Activity activity, LocalDateTime bookingDate) {
        this.id = id;
        this.user = user;
        this.activity = activity;
        this.bookingDate = bookingDate;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private User user; // The owner of the booking 

    @ManyToOne
    private Activity activity;

    private LocalDateTime bookingDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public LocalDateTime getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(LocalDateTime bookingDate) {
        this.bookingDate = bookingDate;
    }

    
    
}
