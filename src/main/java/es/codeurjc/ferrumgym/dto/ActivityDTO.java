package es.codeurjc.ferrumgym.dto;

import es.codeurjc.ferrumgym.model.Activity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

public class ActivityDTO {

    private Long id;
    
    @NotBlank(message = "Activity name is required")
    private String name;
    
    @NotBlank(message = "Description is required")
    private String description;
    
    private String trainer;
    private String schedule;
    
    @PositiveOrZero(message = "Capacity must be zero or a positive number")
    private int capacity;
    
    private int enrolled;
    private String pdfFilename;
    private String image;
    private String imageUrl;

    public ActivityDTO() {}

    public ActivityDTO(Activity activity) {
        this.id = activity.getId();
        this.name = activity.getName();
        this.description = activity.getDescription();
        this.trainer = activity.getTrainer();
        this.schedule = activity.getSchedule();
        this.capacity = activity.getCapacity();
        this.enrolled = activity.getEnrolled();
        this.pdfFilename = activity.getPdfFilename();
        this.image = activity.getImageFilename();

        // Si hay nombre de archivo, generamos la URL dinámica
        if (activity.getImageFilename() != null) {
            this.imageUrl = org.springframework.web.servlet.support.ServletUriComponentsBuilder
                    .fromCurrentContextPath()
                    .path("/api/v1/activities/")
                    .path(activity.getId().toString())
                    .path("/image")
                    .toUriString();
        }
    }

    // Getters y Setters en inglés (Punto 24 de la rúbrica)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getTrainer() { return trainer; }
    public void setTrainer(String trainer) { this.trainer = trainer; }

    public String getSchedule() { return schedule; }
    public void setSchedule(String schedule) { this.schedule = schedule; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    public int getEnrolled() { return enrolled; }
    public void setEnrolledt(int enrolledCount) { this.enrolled = enrolledCount; }

    public String getPdfFilename() { return pdfFilename; }
    public void setPdfFilename(String pdfFilename) { this.pdfFilename = pdfFilename; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}