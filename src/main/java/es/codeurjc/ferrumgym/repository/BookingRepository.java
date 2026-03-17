package es.codeurjc.ferrumgym.repository;

import es.codeurjc.ferrumgym.model.Activity;
import es.codeurjc.ferrumgym.model.Booking;
import es.codeurjc.ferrumgym.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByActivityId(Long activityId);

    // method to check if a booking exists for a user and activity
    boolean existsByUserAndActivity(User user, Activity activity);
}
