package es.codeurjc.ferrumgym.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.nio.charset.StandardCharsets;

@Component
public class JwtTokenProvider {

    // 1. Clave secreta (Para la 0.12.5 es mejor usar una cadena de texto larga convertida a llave)
    private final String JWT_SECRET_STRING = "esta_es_una_clave_secreta_super_larga_para_el_gym_2026_ferrum_gym_security";
    private final SecretKey JWT_SECRET = Keys.hmacShaKeyFor(JWT_SECRET_STRING.getBytes(StandardCharsets.UTF_8));

    // 2. Tiempo de validez (1 hora)
    private static final long JWT_EXPIRATION_MS = 3600000;

    // GENERAR EL TOKEN (Versión 0.12.x)
    public String generateToken(Authentication authentication) {
        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();

        return Jwts.builder()
                .subject(userPrincipal.getUsername()) // Antes era setSubject
                .issuedAt(new Date())                // Antes era setIssuedAt
                .expiration(new Date((new Date()).getTime() + JWT_EXPIRATION_MS)) // Antes era setExpiration
                .signWith(JWT_SECRET)                // Ahora detecta el algoritmo solo
                .compact();
    }

    // OBTENER EL USUARIO (Versión 0.12.x)
    public String getUsernameFromToken(String token) {
        return Jwts.parser()
                .verifyWith(JWT_SECRET) // Antes era setSigningKey
                .build()
                .parseSignedClaims(token) // Antes era parseClaimsJws
                .getPayload()             // Antes era getBody
                .getSubject();
    }

    // VALIDAR EL TOKEN
    public boolean validateToken(String authToken) {
        try {
            Jwts.parser()
                .verifyWith(JWT_SECRET)
                .build()
                .parseSignedClaims(authToken);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            System.out.println("Error con el token JWT: " + e.getMessage());
        }
        return false;
    }
}