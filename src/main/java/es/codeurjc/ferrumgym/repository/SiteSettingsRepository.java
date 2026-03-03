package es.codeurjc.ferrumgym.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import es.codeurjc.ferrumgym.model.SiteSettings;

public interface SiteSettingsRepository extends JpaRepository<SiteSettings, Long> {
}
