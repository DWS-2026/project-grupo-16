package es.codeurjc.ferrumgym.model;
import jakarta.persistence.*;


@Entity
public class Review {

    public Review() {}

    public Review(Long id, String comment, int rating, User user, Activity activity) {
        this.id = id;
        this.comment = comment;
        this.rating = rating;
        this.user = user;
        this.activity = activity;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String comment;
    private int rating;

    @ManyToOne
    private User user; // Only the owner can edit/delete

    @ManyToOne
    private Activity activity;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
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


}
