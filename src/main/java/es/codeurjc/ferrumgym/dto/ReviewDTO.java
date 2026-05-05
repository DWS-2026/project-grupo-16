package es.codeurjc.ferrumgym.dto;

public record ReviewDTO(
    Long id,
    String comment,
    Integer rating,
    String userName,     // Solo el nombre, no el objeto User entero
    String activityName, // Solo el nombre, no el objeto Activity entero
    String imageUrl
) {}