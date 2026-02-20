package es.codeurjc.ferrumgym.model;
import jakarta.persistence.*;
import java.util.List;

@Entity
public class Activity {

    public Activity(Long id, String name, String description, byte[] image, List<Booking> bookings) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.image = image;
        this.bookings = bookings;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    @Column(columnDefinition = "TEXT")
    private String description; // Will be Rich Text in Practice 3 [cite: 239, 554]

    @Lob
    private byte[] image; // Activity photo [cite: 238, 552, 657]

    @OneToMany(mappedBy = "activity", cascade = CascadeType.ALL)
    private List<Booking> bookings;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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


}
