package es.codeurjc.ferrumgym.controller;

import es.codeurjc.ferrumgym.model.SiteSettings;
import es.codeurjc.ferrumgym.service.SiteSettingsService; 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {

    @Autowired
    private SiteSettingsService siteSettingsService; 

    // This method will be called before every controller method, adding the "settings" attribute to the model
    @ModelAttribute("settings")
    public SiteSettings globalSettings() {
        // Llamamos al Service para obtener los datos
        return siteSettingsService.findAll().stream().findFirst().orElse(null); 
    }
}
