package es.codeurjc.ferrumgym.model;

import jakarta.persistence.*;

@Entity
@Table(name = "activities")
public class Activity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String trainer; //atm string, afterwards we will relate it to User
    private String schedule;
    private int capacity;
    private int enrolled; // Gente apuntada

    public Activity() {}

    public Activity(String name, String trainer, String schedule, int capacity, int enrolled) {
        this.name = name;
        this.trainer = trainer;
        this.schedule = schedule;
        this.capacity = capacity;
        this.enrolled = enrolled;
    }

    // Método auxiliar (Lógica de negocio)
    public boolean isFull() {
        return this.enrolled >= this.capacity;
    }

    // ... Genera Getters y Setters aquí ...
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    // ... resto ...
}
