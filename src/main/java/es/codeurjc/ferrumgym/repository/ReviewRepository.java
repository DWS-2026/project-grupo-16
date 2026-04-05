package es.codeurjc.ferrumgym.repository;
import es.codeurjc.ferrumgym.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;

// Interface for managing activity reviews
public interface ReviewRepository extends JpaRepository<Review, Long> {
    
}

