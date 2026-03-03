package es.codeurjc.ferrumgym.controller;

import es.codeurjc.ferrumgym.model.SiteSettings;
import es.codeurjc.ferrumgym.repository.SiteSettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {

    @Autowired
    private SiteSettingsRepository siteSettingsRepository;

    // This method will be called before every controller method, adding the "settings" attribute to the model
    @ModelAttribute("settings")
    public SiteSettings globalSettings() {
        return siteSettingsRepository.findAll().stream().findFirst().orElse(null);
    }
}
