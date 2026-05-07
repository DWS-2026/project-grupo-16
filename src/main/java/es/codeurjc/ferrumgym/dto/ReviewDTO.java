package es.codeurjc.ferrumgym.dto;

public record ReviewDTO(
    Long id,
    String comment,
    Integer rating,
    String userName,     
    String activityName, 
    String imageUrl
) {}