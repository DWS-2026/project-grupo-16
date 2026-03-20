package es.codeurjc.ferrumgym.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // 1. Mantenemos tu encriptador de contraseñas (¡Esencial para el siguiente paso!)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        
        http.authorizeHttpRequests(authorize -> authorize
            
            // 2. ARCHIVOS ESTÁTICOS Y PÚBLICOS (Para que la web no se vea sin estilos)
            .requestMatchers("/css/**", "/js/**", "/assets/**").permitAll()
            
            // 3. RUTAS DE VISITANTES (No logueados)
            .requestMatchers("/", "/prices", "/register", "/login", "/forgot-password").permitAll()
            .requestMatchers(HttpMethod.GET, "/activity/**").permitAll() // Ver detalles de actividad
            .requestMatchers(HttpMethod.GET, "/user/*/image").permitAll() // Ver avatares en las reseñas
            
            // 4. RUTAS DE USUARIOS REGISTRADOS (User y Admin)
            .requestMatchers("/user-profile", "/edit-profile/**", "/booking/cancel/**").hasAnyRole("USER", "ADMIN")
            .requestMatchers(HttpMethod.POST, "/activity/*/review", "/activity/*/book").hasAnyRole("USER", "ADMIN")
            
            // 5. RUTAS DEL ADMINISTRADOR (El Profe)
            .requestMatchers("/admin-dashboard/**").hasRole("ADMIN")
            
            // 6. CUALQUIER OTRA COSA (Por seguridad, pedimos login)
            .anyRequest().authenticated()
        )
        // 7. CONFIGURACIÓN DEL LOGIN AUTOMÁTICO DE SPRING
        .formLogin(formLogin -> formLogin
            .loginPage("/login") // Le decimos que use nuestro HTML, no el feo por defecto
            .failureUrl("/login?error") // A dónde ir si falla la contraseña
            .defaultSuccessUrl("/", true) // A dónde ir si acierta
            .permitAll()
        )
        // 8. CONFIGURACIÓN DEL LOGOUT
        .logout(logout -> logout
            .logoutUrl("/logout")
            .logoutSuccessUrl("/") // Volver a la home al salir
            .permitAll()
        );

        // ¡OJO! Ya NO desactivamos el CSRF. Lo dejamos activo por defecto para cumplir la rúbrica.

        return http.build();
    }
}