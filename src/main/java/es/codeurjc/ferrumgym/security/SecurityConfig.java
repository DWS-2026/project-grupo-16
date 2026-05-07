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

    // Defines the password encoding algorithm (BCrypt)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Exposes the AuthenticationManager as a Bean for the AuthRestController
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    // --- CHAIN 0: EXCLUSIVE FOR SWAGGER (Highest priority) ---
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

    // --- CHAIN 1: REST API ---
    @Bean
    @Order(1)
    public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/api/**") // Covers /api/v1 and /api/auth endpoints
                .authorizeHttpRequests(auth -> auth
                        // Public API routes - Allow login and public queries
                        .requestMatchers("/api/auth/login").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/activities/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/users").permitAll()
                        // NEW MATCHERS FOR IMAGES - Allow public access to images
                        .requestMatchers(HttpMethod.GET, "/api/v1/activities/*/image").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/reviews/*/image").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/users/*/image").permitAll()
                        // Protected routes (ADMIN only)
                        .requestMatchers(HttpMethod.POST, "/api/v1/activities/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/activities/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/activities/**").hasRole("ADMIN")
                        .requestMatchers("/api/v1/users/**").hasRole("ADMIN")
                        .anyRequest().authenticated())
                .csrf(csrf -> csrf.disable())
                .httpBasic(Customizer.withDefaults())
                // ANTI-HTML CONFIGURATION FOR THE API - Returns JSON errors instead of HTML pages
                .exceptionHandling(ex -> ex
                        // If NOT logged in -> Return 401 Unauthorized
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType("application/json");
                            response.getWriter().write("{\"error\": \"You are not authenticated\"}");
                        })
                        // If logged in but NOT ADMIN -> Return 403 Forbidden
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            response.setContentType("application/json");
                            response.getWriter().write("{\"error\": \"You do not have administrator privileges\"}");
                        }))
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // DISABLE Basic Auth: Now we only want JWT authentication for the API
            .httpBasic(basic -> basic.disable());

        // ADD JWT FILTER - Intercepts requests to validate the token before proceeding
        http.addFilterBefore(jwtRequestFilter, org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // --- CHAIN 2: WEB INTERFACE ---
    @Bean
    @Order(2)
    public SecurityFilterChain webFilterChain(HttpSecurity http) throws Exception {
        http
                // SECURITY FIX: Add Strict Content Security Policy (CSP) header
				.headers(headers -> headers
									.contentSecurityPolicy(csp -> csp
										.policyDirectives("default-src 'self'; " +
														"script-src 'self' https://cdn.jsdelivr.net https://cdn.quilljs.com; " +
														// Added 'unsafe-inline' only to styles so Quill editor can be rendered properly
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