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

        // 1. Look for the token in the "Authorization" header
        final String header = request.getHeader("Authorization");
        String username = null;
        String jwt = null;

        // The token must start with "Bearer " prefix
        if (header != null && header.startsWith("Bearer ")) {
            jwt = header.substring(7);
            try {
                username = jwtTokenProvider.getUsernameFromToken(jwt);
            } catch (Exception e) {
                logger.error("Could not extract user from token: " + e.getMessage());
            }
        }

        // 2. If we have a user and they are not already authenticated in this request
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            // 3. If the token is valid, put it in the security context
            if (jwtTokenProvider.validateToken(jwt)) {
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                
                // This is where the user is granted access for this specific request
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        // 4. Continue with the filter chain execution
        filterChain.doFilter(request, response);
    }
}