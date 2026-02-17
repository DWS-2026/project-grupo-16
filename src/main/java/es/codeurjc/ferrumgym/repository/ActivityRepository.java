package es.codeurjc.ferrumgym.repository;

import es.codeurjc.ferrumgym.model.Activity;
import org.springframework.data.jpa.repository.JpaRepository;

// Interface for managing gym activities in MySQL [cite: 326]
public interface ActivityRepository extends JpaRepository<Activity, Long> {
}
