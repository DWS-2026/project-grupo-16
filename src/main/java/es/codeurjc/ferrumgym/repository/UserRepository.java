package es.codeurjc.ferrumgym.repository;

import es.codeurjc.ferrumgym.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

// Interface for managing User entities in the database [cite: 204]
public interface UserRepository extends JpaRepository<User, Long> {
    
    // This method is essential for Spring Security to find users during login 
    Optional<User> findByEmail(String email);
}
