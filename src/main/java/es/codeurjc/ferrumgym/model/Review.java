package es.codeurjc.ferrumgym.model;

import jakarta.persistence.*;

@Entity
@Table(name = "reviews")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String author; // Por ahora String
    private String className;
    private int stars; // 1 a 5

    @Column(length = 1000) // Para permitir textos largos
    private String comment;

    public Review() {}

    public Review(String author, String className, int stars, String comment) {
        this.author = author;
        this.className = className;
        this.stars = stars;
        this.comment = comment;
    }

    // ... Genera Getters y Setters aquí ...
}
