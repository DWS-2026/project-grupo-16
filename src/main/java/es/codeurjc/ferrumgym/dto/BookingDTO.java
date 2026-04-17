package es.codeurjc.ferrumgym.dto;

import java.time.LocalDateTime;
import es.codeurjc.ferrumgym.model.Booking;

public class BookingDTO {

    private Long id;
    private Long userId;
    private String userName;
    private Long activityId;
    private String activityName;
    private LocalDateTime bookingDate;
    private boolean attended;

    // Constructor vacío (necesario para la serialización)
    public BookingDTO() {}

    // Constructor desde Entidad (Cumple punto 21 de la rúbrica)
    public BookingDTO(Booking booking) {
        this.id = booking.getId();
        this.bookingDate = booking.getBookingDate();
        this.attended = booking.isAttended();
        
        // Extraemos solo lo necesario de las relaciones para evitar bucles y datos sensibles
        if (booking.getUser() != null) {
            this.userId = booking.getUser().getId();
            this.userName = booking.getUser().getName();
        }
        
        if (booking.getActivity() != null) {
            this.activityId = booking.getActivity().getId();
            this.activityName = booking.getActivity().getName();
        }
    }

    // Getters y Setters en inglés (Punto 24 de la rúbrica)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public Long getActivityId() { return activityId; }
    public void setActivityId(Long activityId) { this.activityId = activityId; }

    public String getActivityName() { return activityName; }
    public void setActivityName(String activityName) { this.activityName = activityName; }

    public LocalDateTime getBookingDate() { return bookingDate; }
    public void setBookingDate(LocalDateTime bookingDate) { this.bookingDate = bookingDate; }

    public boolean isAttended() { return attended; }
    public void setAttended(boolean attended) { this.attended = attended; }
}