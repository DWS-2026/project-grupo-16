package es.codeurjc.ferrumgym;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Le decimos a Spring: "Cuando alguien pida algo que empiece por /uploads/..."
        registry.addResourceHandler("/uploads/**")
                // "...búscalo físicamente en la carpeta 'uploads' de la raíz del proyecto"
                .addResourceLocations("file:uploads/");
    }
}