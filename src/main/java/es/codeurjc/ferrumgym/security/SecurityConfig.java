package es.codeurjc.ferrumgym.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // 1. Allow all requests without login (for development)
            .authorizeHttpRequests(authorize -> authorize
                .anyRequest().permitAll()
            )
            // 2. Disable CSRF protection so POST forms work without 403 Forbidden
            .csrf(csrf -> csrf.disable());

        return http.build();
    }
}
