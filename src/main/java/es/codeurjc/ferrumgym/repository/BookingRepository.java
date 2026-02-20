package es.codeurjc.ferrumgym.repository;
import es.codeurjc.ferrumgym.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

// Interface for managing user bookings 
public interface BookingRepository extends JpaRepository<Booking, Long> {
}