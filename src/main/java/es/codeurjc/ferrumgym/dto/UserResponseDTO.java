package es.codeurjc.ferrumgym.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record UserResponseDTO(
    Long id,
    String name,
    String email,
    List<String> roles,
    String imageUrl,
    
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) 
    String password
) {}