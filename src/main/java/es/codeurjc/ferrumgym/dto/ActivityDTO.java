package es.codeurjc.ferrumgym.dto;

import es.codeurjc.ferrumgym.model.Activity;

public class ActivityDTO {

    private Long id;
    private String name;
    private String description;
    private String trainer;
    private String schedule;
    private int capacity;
    private int enrolledCount;
    private String pdfFilename;

    // Constructor vacío
    public ActivityDTO() {}

    // Constructor desde Entidad (Punto 21 de la rúbrica)
    public ActivityDTO(Activity activity) {
        this.id = activity.getId();
        this.name = activity.getName();
        this.description = activity.getDescription();
        this.trainer = activity.getTrainer();
        this.schedule = activity.getSchedule();
        this.capacity = activity.getCapacity();
        this.enrolledCount = activity.getEnrolled();
        this.pdfFilename = activity.getPdfFilename();
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

    public int getEnrolledCount() { return enrolledCount; }
    public void setEnrolledCount(int enrolledCount) { this.enrolledCount = enrolledCount; }

    public String getPdfFilename() { return pdfFilename; }
    public void setPdfFilename(String pdfFilename) { this.pdfFilename = pdfFilename; }
}