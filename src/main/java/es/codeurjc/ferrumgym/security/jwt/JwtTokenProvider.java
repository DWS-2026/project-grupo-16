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

    // 1. Secret key (For version 0.12.5 it is better to use a long text string converted to a cryptographic key)
    private final String JWT_SECRET_STRING = "esta_es_una_clave_secreta_super_larga_para_el_gym_2026_ferrum_gym_security";
    private final SecretKey JWT_SECRET = Keys.hmacShaKeyFor(JWT_SECRET_STRING.getBytes(StandardCharsets.UTF_8));

    // 2. Validity time (Set to 1 hour for security purposes)
    private static final long JWT_EXPIRATION_MS = 3600000;

    // GENERATE TOKEN (Version 0.12.x syntax)
    public String generateToken(Authentication authentication) {
        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();

        return Jwts.builder()
                .subject(userPrincipal.getUsername()) // Previously setSubject
                .issuedAt(new Date())                 // Previously setIssuedAt
                .expiration(new Date((new Date()).getTime() + JWT_EXPIRATION_MS)) // Previously setExpiration
                .signWith(JWT_SECRET)                 // Now it detects the signing algorithm automatically
                .compact();
    }

    // GET USER FROM TOKEN (Version 0.12.x syntax)
    public String getUsernameFromToken(String token) {
        return Jwts.parser()
                .verifyWith(JWT_SECRET) // Previously setSigningKey
                .build()
                .parseSignedClaims(token) // Previously parseClaimsJws
                .getPayload()             // Previously getBody
                .getSubject();
    }

    // VALIDATE TOKEN - Checks if it is properly signed and not expired
    public boolean validateToken(String authToken) {
        try {
            Jwts.parser()
                .verifyWith(JWT_SECRET)
                .build()
                .parseSignedClaims(authToken);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            System.out.println("JWT token error: " + e.getMessage());
        }
        return false;
    }
}