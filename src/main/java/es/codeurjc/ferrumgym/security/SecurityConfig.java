package es.codeurjc.ferrumgym.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.Customizer;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // --- 1. CONFIGURACIÓN PARA LA API REST ---
    @Bean
    @Order(1)
    public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {

        http
            .securityMatcher("/api/v1/**") // Punto 20: Prefijo obligatorio /api/v1/ 
            .authorizeHttpRequests(authorize -> authorize
                // Endpoints públicos de la API
                .requestMatchers(HttpMethod.GET, "/api/v1/activities/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/v1/users").permitAll() // Registro
                
                // Endpoints de Administrador en la API
                .requestMatchers(HttpMethod.POST, "/api/v1/activities/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/v1/activities/**").hasRole("ADMIN")
                .requestMatchers("/api/v1/users/**").hasRole("ADMIN")
                
                // Endpoints de Usuarios registrados
                .requestMatchers(HttpMethod.POST, "/api/v1/bookings/**").hasAnyRole("USER", "ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/v1/reviews/**").hasAnyRole("USER", "ADMIN")
                
                .anyRequest().authenticated()
            );

        // Punto 23: Desactivar CSRF solo en la API REST [cite: 503, 718]
        http.csrf(csrf -> csrf.disable());

        // Habilitar Autenticación Básica para probar con Postman 
        http.httpBasic(Customizer.withDefaults());

        // API sin estado (No guarda sesiones en el servidor)
        http.sessionManagement(management -> 
            management.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // Desactivar el formulario de login para la API
        http.formLogin(form -> form.disable());

        return http.build();
    }

    // --- 2. CONFIGURACIÓN PARA LA INTERFAZ WEB ---
    @Bean
    @Order(2)
    public SecurityFilterChain webFilterChain(HttpSecurity http) throws Exception {

        http.authorizeHttpRequests(authorize -> authorize
            // Archivos estáticos
            .requestMatchers("/css/**", "/js/**", "/assets/**", "/docs/**").permitAll()

            // Control de acceso para administradores (Web) 
            .requestMatchers("/admin-dashboard/**", "/admin-class/**", "/admin-users/**", "/site-settings").hasRole("ADMIN")
            .requestMatchers("/activity/new", "/activity/edit/**", "/activity/delete/**").hasRole("ADMIN")

            // Rutas públicas web
            .requestMatchers("/", "/prices", "/register", "/login", "/forgot-password").permitAll()
            .requestMatchers(HttpMethod.GET, "/activity/**").permitAll()

            // Rutas para usuarios registrados (Web)
            .requestMatchers("/user-profile", "/edit-profile/**", "/booking/cancel/**").hasAnyRole("USER", "ADMIN")
            .requestMatchers(HttpMethod.POST, "/activity/*/review", "/activity/*/book").hasAnyRole("USER", "ADMIN")

            .anyRequest().authenticated()
        )
        // Punto 23: CSRF sigue activo por defecto para la web 
        .formLogin(formLogin -> formLogin
            .loginPage("/login")
            .failureUrl("/login?error")
            .defaultSuccessUrl("/", true)
            .permitAll()
        )
        .logout(logout -> logout
            .logoutUrl("/logout")
            .logoutSuccessUrl("/")
            .permitAll()
        );

        return http.build();
    }
}