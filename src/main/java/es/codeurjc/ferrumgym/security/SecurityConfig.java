package es.codeurjc.ferrumgym.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // 1. Permite entrar a todas las páginas sin pedir contraseña
            .authorizeHttpRequests(authorize -> authorize
                .anyRequest().permitAll()
            )
            // 2. Desactiva la protección CSRF para que no te dé Error 403 al enviar formularios
            .csrf(csrf -> csrf.disable());

        return http.build();
    }

    // 3. Le enseñamos a Spring cómo encriptar las contraseñas del DatabaseInitializer
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
