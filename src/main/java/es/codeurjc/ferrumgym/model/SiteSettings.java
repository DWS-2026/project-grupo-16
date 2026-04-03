package es.codeurjc.ferrumgym.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class SiteSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String gymName;
    private String contactEmail;
    private String contactPhone;
    private String address;
    private String weekdaysHours;
    private String weekendsHours;
    
    @ManyToOne
    private User updatedBy; // El administrador que guardó los ajustes

    // Constructor
    public SiteSettings() {}

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getGymName() { return gymName; }
    public void setGymName(String gymName) { this.gymName = gymName; }

    public String getContactEmail() { return contactEmail; }
    public void setContactEmail(String contactEmail) { this.contactEmail = contactEmail; }

    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getWeekdaysHours() { return weekdaysHours; }
    public void setWeekdaysHours(String weekdaysHours) { this.weekdaysHours = weekdaysHours; }

    public String getWeekendsHours() { return weekendsHours; }
    public void setWeekendsHours(String weekendsHours) { this.weekendsHours = weekendsHours; }

    public User getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(User updatedBy) { this.updatedBy = updatedBy; }
}
