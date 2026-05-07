package es.codeurjc.ferrumgym.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import es.codeurjc.ferrumgym.security.jwt.JwtRequestFilter;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    // --- CADENA 0: EXCLUSIVA PARA SWAGGER (Máxima prioridad) ---
    @Bean
    @Order(0)
    public SecurityFilterChain swaggerFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/v3/api-docs*/**", "/swagger-ui/**", "/swagger-ui.html")
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        return http.build();
    }

    // --- CADENA 1: API REST ---
    @Bean
    @Order(1)
    public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/api/**") //Abarca /api/v1 y /api/auth
                .authorizeHttpRequests(auth -> auth
                        // Rutas públicas de la API - Permitimos el login y las consultas públicas
                        .requestMatchers("/api/auth/login").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/activities/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/users").permitAll()
                        // LOS NUEVOS MATCHERS PARA IMÁGENES
                        .requestMatchers(HttpMethod.GET, "/api/v1/activities/*/image").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/reviews/*/image").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/users/*/image").permitAll()
                        // Rutas protegidas (ADMIN)
                        .requestMatchers(HttpMethod.POST, "/api/v1/activities/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/activities/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/activities/**").hasRole("ADMIN")
                        .requestMatchers("/api/v1/users/**").hasRole("ADMIN")
                        .anyRequest().authenticated())
                .csrf(csrf -> csrf.disable())
                .httpBasic(Customizer.withDefaults())
                // CONFIGURACIÓN ANTI-HTML PARA LA API
                .exceptionHandling(ex -> ex
                        // Si NO está logueado -> 401
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType("application/json");
                            response.getWriter().write("{\"error\": \"You are not authenticated\"}");
                        })
                        // Si está logueado pero no es ADMIN -> 403
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            response.setContentType("application/json");
                            response.getWriter().write("{\"error\": \"You do not have administrator privileges\"}");
                        }))
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // DESACTIVAMOS Basic Auth: Ahora solo queremos que entre por JWT
            .httpBasic(basic -> basic.disable());

        // AÑADIMOS EL FILTRO JWT
        http.addFilterBefore(jwtRequestFilter, org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain webFilterChain(HttpSecurity http) throws Exception {
        http
                // SECURITY FIX: Add Content Security Policy (CSP) header
                // SECURITY FIX: Add Strict Content Security Policy (CSP) header
                // SECURITY FIX: Add Strict Content Security Policy (CSP) header
				.headers(headers -> headers
									.contentSecurityPolicy(csp -> csp
										.policyDirectives("default-src 'self'; " +
														"script-src 'self' https://cdn.jsdelivr.net https://cdn.quilljs.com; " +
														// Añadido 'unsafe-inline' solo a los estilos para que Quill pueda dibujarse
														"style-src 'self' 'unsafe-inline' https://cdn.jsdelivr.net https://cdn.quilljs.com; " +
														"img-src 'self' data: blob:; " +
														"font-src 'self' https://cdn.jsdelivr.net data:; " +
														"frame-ancestors 'self'; " +
														"form-action 'self';")
									)
								)
                // --- End of CSP header ---
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/css/**", "/js/**", "/assets/**", "/docs/**").permitAll()
                        .requestMatchers("/admin-dashboard/**", "/admin-class/**", "/admin-users/**", "/site-settings/**", "/activity/edit/**", "/activity/new/**", "/admin/user/edit/**").hasRole("ADMIN")
                        .requestMatchers("/", "/login", "/register", "/prices").permitAll()
                        .requestMatchers(HttpMethod.GET, "/activity/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/review/*/image").permitAll()
                        .requestMatchers("/review/delete/**").hasRole("ADMIN")
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/", true)
                        .permitAll())
                .logout(out -> out.logoutSuccessUrl("/").permitAll());
        return http.build();
    }
}
