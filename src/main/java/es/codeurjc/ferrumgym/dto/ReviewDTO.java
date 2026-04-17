package es.codeurjc.ferrumgym.dto;

import es.codeurjc.ferrumgym.model.Review;

public class ReviewDTO {

    private Long id;
    private String comment;
    private int rating;
    private Long userId;
    private String userName;
    private Long activityId;
    private String activityName;
    private boolean hasImage;

    // Constructor vacío para serialización
    public ReviewDTO() {}

    // Constructor para transformar Entidad -> DTO (Cumple punto 21)
    public ReviewDTO(Review review) {
        this.id = review.getId();
        this.comment = review.getComment();
        this.rating = review.getRating();
        this.hasImage = review.getHasImage();

        if (review.getUser() != null) {
            this.userId = review.getUser().getId();
            this.userName = review.getUser().getName();
        }

        if (review.getActivity() != null) {
            this.activityId = review.getActivity().getId();
            this.activityName = review.getActivity().getName();
        }
    }

    // Getters y Setters (En inglés según punto 24)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public Long getActivityId() { return activityId; }
    public void setActivityId(Long activityId) { this.activityId = activityId; }

    public String getActivityName() { return activityName; }
    public void setActivityName(String activityName) { this.activityName = activityName; }

    public boolean isHasImage() { return hasImage; }
    public void setHasImage(boolean hasImage) { this.hasImage = hasImage; }
}