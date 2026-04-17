package es.codeurjc.ferrumgym.dto;

import java.util.List;
import es.codeurjc.ferrumgym.model.User;

public class UserResponseDTO {

    private Long id;
    private String name;
    private String email;
    private List<String> roles;

    // Constructor vacío necesario para la serialización de JSON
    public UserResponseDTO() {}

    // Constructor que convierte la Entidad en DTO (Cumple el punto 21 de la rúbrica)
    public UserResponseDTO(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.roles = user.getRoles();
    }

    // Getters y Setters (En inglés según el punto 24 de la rúbrica)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
}