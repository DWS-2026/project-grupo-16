package es.codeurjc.ferrumgym.repository;

import es.codeurjc.ferrumgym.model.Tariff;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TariffRepository extends JpaRepository<Tariff, Long> {
    // Vacío, Spring Data hace la magia por debajo.
}
//Al poner extends JpaRepository<Tariff, Long>, le estamos diciendo a Spring: 
//"Créame todas las herramientas típicas de base de datos para la tabla Tariff, sabiendo que su ID es un Long