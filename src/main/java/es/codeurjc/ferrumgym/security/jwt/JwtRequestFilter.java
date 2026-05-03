package es.codeurjc.ferrumgym.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 1. Buscamos el token en la cabecera "Authorization"
        final String header = request.getHeader("Authorization");
        String username = null;
        String jwt = null;

        // El token debe empezar por "Bearer "
        if (header != null && header.startsWith("Bearer ")) {
            jwt = header.substring(7);
            try {
                username = jwtTokenProvider.getUsernameFromToken(jwt);
            } catch (Exception e) {
                logger.error("No se pudo extraer el usuario del token: " + e.getMessage());
            }
        }

        // 2. Si tenemos usuario y no está ya autenticado en esta petición
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            // 3. Si el token es válido, lo metemos en el contexto de seguridad
            if (jwtTokenProvider.validateToken(jwt)) {
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                
                // Aquí es donde el portero le da las llaves al usuario para esta petición
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        // 4. Continuar con la petición
        filterChain.doFilter(request, response);
    }
}