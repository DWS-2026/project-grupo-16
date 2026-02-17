package es.codeurjc.ferrumgym.model;
import jakarta.persistence.*;
import java.util.List;

@Entity
public class User {
    
    public User(Long id, String name, String email, String encodedPassword, List<String> roles, byte[] image,
            List<Booking> bookings, List<Review> reviews) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.encodedPassword = encodedPassword;
        this.roles = roles;
        this.image = image;
        this.bookings = bookings;
        this.reviews = reviews;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;
    private String email;
    private String encodedPassword;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> roles; // ROLE_USER, ROLE_ADMIN [cite: 217, 532]

    @Lob
    private byte[] image; // Stored as LONGBLOB in MySQL 

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Booking> bookings; // Ownership of bookings 

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Review> reviews; // Ownership of reviews 

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEncodedPassword() {
        return encodedPassword;
    }

    public void setEncodedPassword(String encodedPassword) {
        this.encodedPassword = encodedPassword;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public List<Booking> getBookings() {
        return bookings;
    }

    public void setBookings(List<Booking> bookings) {
        this.bookings = bookings;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    
}