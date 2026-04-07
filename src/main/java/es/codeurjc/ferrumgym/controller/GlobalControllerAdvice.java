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

    @ModelAttribute("settings")
    public SiteSettings globalSettings() {
        return siteSettingsService.getSettings();
    }
}