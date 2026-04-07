package es.codeurjc.ferrumgym.model;

public class SiteSettings {

// Valores por defecto (se cargarán automáticamente al arrancar la app)
    private String gymName = "Ferrum Gym";
    private String contactEmail = "info@ferrumgym.com";
    private String contactPhone = "+34 912 345 678";
    private String address = "Calle Tulipán s/n. 28933 Móstoles (Madrid)";
    private String weekdaysHours = "07:00 - 23:00";
    private String weekendsHours = "09:00 - 21:00";
    
    private String updatedBy = "Sistema";

    public SiteSettings() {}

    // --- GETTERS Y SETTERS ---
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

    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }
}