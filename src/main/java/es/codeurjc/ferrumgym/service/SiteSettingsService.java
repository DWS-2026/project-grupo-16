package es.codeurjc.ferrumgym.service;

import es.codeurjc.ferrumgym.model.SiteSettings;
import org.springframework.stereotype.Service;

@Service
public class SiteSettingsService {

    /* * DECISIÓN DE DISEÑO:
     * La configuración del sitio se gestiona en memoria y no como un @Entity.
     * Al ser un objeto único que no requiere operaciones de Listado, Creación múltiple 
     * ni Borrado, excluirlo de la base de datos asegura el cumplimiento estricto 
     * de la arquitectura de entidades del proyecto y evita CRUDs incompletos.
     */
    private SiteSettings currentSettings = new SiteSettings();

    public SiteSettings getSettings() {
        return currentSettings;
    }

    public void saveSettings(SiteSettings newSettings) {
        this.currentSettings = newSettings;
    }
}