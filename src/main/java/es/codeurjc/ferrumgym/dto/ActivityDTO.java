package es.codeurjc.ferrumgym.dto;

public record ActivityDTO(
    Long id,
    String name,
    String description,
    String trainer,
    String schedule,
    Integer capacity,
    Integer enrolledCount,
    String pdfFilename,
    String imageUrl
) {}