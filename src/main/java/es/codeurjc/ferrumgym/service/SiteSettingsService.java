package es.codeurjc.ferrumgym.service;

import es.codeurjc.ferrumgym.model.SiteSettings;
import es.codeurjc.ferrumgym.repository.SiteSettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SiteSettingsService {

    @Autowired
    private SiteSettingsRepository siteSettingsRepository;

    public List<SiteSettings> findAll() {
        return siteSettingsRepository.findAll();
    }

    public void save(SiteSettings settings) {
        siteSettingsRepository.save(settings);
    }
}
