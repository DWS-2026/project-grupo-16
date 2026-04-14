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

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
	@Order(1)
	public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {

		http
				.securityMatcher("/api/**");
				//.exceptionHandling(handling -> handling.authenticationEntryPoint(unauthorizedHandlerJwt));

		http
				.authorizeHttpRequests(authorize -> authorize
						// PRIVATE ENDPOINTS
						// Images
						//.requestMatchers(HttpMethod.PUT, "/api/images/*/media").hasRole("USER")
						//.requestMatchers(HttpMethod.DELETE, "/api/books/*/images/*").hasRole("USER")
						// Books
						//.requestMatchers(HttpMethod.POST, "/api/books/**").hasRole("USER")
						//.requestMatchers(HttpMethod.PUT, "/api/books/**").hasRole("USER")
						//.requestMatchers(HttpMethod.DELETE, "/api/books/**").hasRole("ADMIN")
						// Shops
						//.requestMatchers(HttpMethod.PUT, "/api/shops/**").hasRole("ADMIN")
						//.requestMatchers(HttpMethod.PUT, "/api/shops/**").hasRole("ADMIN")
						//.requestMatchers(HttpMethod.DELETE, "/api/shops/**").hasRole("ADMIN")
						// PUBLIC ENDPOINTS
						.anyRequest().permitAll());

		// Disable Form login Authentication
		http.formLogin(formLogin -> formLogin.disable());

		// Disable CSRF protection (it is difficult to implement in REST APIs)
		http.csrf(csrf -> csrf.disable());

		// Disable Basic Authentication
		http.httpBasic(httpBasic -> httpBasic.disable());

		// Stateless session
		http.sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

		// Add JWT Token filter
		//http.addFilterBefore(new JwtRequestFilter(userDetailService, jwtTokenProvider),
				//UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

    @Bean
    @Order(2)
    public SecurityFilterChain webFilterChain(HttpSecurity http) throws Exception {
        
        http.authorizeHttpRequests(authorize -> authorize
            // 2. ARCHIVOS ESTÁTICOS Y PÚBLICOS (Para que la web no se vea sin estilos)
            .requestMatchers("/css/**", "/js/**", "/assets/**", "/docs/**").permitAll()
            
            // 3. RUTAS DE VISITANTES (No logueados)
            .requestMatchers("/", "/prices", "/register", "/login", "/forgot-password").permitAll()
            .requestMatchers(HttpMethod.GET, "/activity/**").permitAll() // Ver detalles de actividad
            .requestMatchers(HttpMethod.GET, "/user/*/image").permitAll() // Ver avatares en las reseñas
            .requestMatchers(HttpMethod.GET, "/review/*/image").permitAll() // Para ver las fotos de reseñas
            
            // 4. RUTAS DE USUARIOS REGISTRADOS (User y Admin)
            .requestMatchers("/user-profile", "/edit-profile/**", "/booking/cancel/**").hasAnyRole("USER", "ADMIN")
            .requestMatchers(HttpMethod.POST, "/activity/*/review", "/activity/*/book").hasAnyRole("USER", "ADMIN")
            
            // 5. RUTAS DEL ADMINISTRADOR (¡BLINDADAS!)
            .requestMatchers("/admin-dashboard/**", "/admin-class/**", "/admin-users/**", "/site-settings").hasRole("ADMIN")
            .requestMatchers("/activity/new", "/activity/edit/**", "/activity/delete/**").hasRole("ADMIN")
            .requestMatchers("/admin/user/**", "/review/delete/**").hasRole("ADMIN")
            
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

        // Ya NO desactivamos el CSRF. Lo dejamos activo por defecto para cumplir la rúbrica.

        return http.build();
    }
}