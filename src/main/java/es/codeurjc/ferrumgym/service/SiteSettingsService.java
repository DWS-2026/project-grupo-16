package es.codeurjc.ferrumgym.service;

import es.codeurjc.ferrumgym.model.SiteSettings;
import org.springframework.stereotype.Service;

@Service
public class SiteSettingsService {

    /* * DESIGN DECISION:
     * Site configuration is managed in memory rather than as an @Entity.
     * Being a singleton object that requires no Listing, Multiple Creation, 
     * or Deletion operations, excluding it from the database ensures strict compliance 
     * with the project's entity architecture and avoids incomplete CRUD operations.
     */
    private SiteSettings currentSettings = new SiteSettings();

    public SiteSettings getSettings() {
        return currentSettings;
    }

    public void saveSettings(SiteSettings newSettings) {
        this.currentSettings = newSettings;
    }
}