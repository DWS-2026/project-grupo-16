package es.codeurjc.ferrumgym.dto;

import java.util.List;

public record UserResponseDTO(
    Long id,
    String name,
    String email,
    List<String> roles,
    String imageUrl
) {}