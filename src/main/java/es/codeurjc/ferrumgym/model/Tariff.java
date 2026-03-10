package es.codeurjc.ferrumgym.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Tariff {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private double price;
    private String period; // Para guardar si es "/mo", "/day" o "/pack"
    private String description;

    // 1. Constructor vacío (¡Es obligatorio para que Spring Boot y la BBDD se entiendan!)
    public Tariff() {
    }

    // 2. Constructor para cuando queramos crear tarifas nosotros desde el código
    public Tariff(String name, double price, String period, String description) {
        this.name = name;
        this.price = price;
        this.period = period;
        this.description = description;
    }

    // 3. Getters y Setters (Para que Spring pueda leer y modificar los datos)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getPeriod() { return period; }
    public void setPeriod(String period) { this.period = period; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}