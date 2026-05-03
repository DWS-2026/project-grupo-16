package es.codeurjc.ferrumgym.security.jwt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthRestController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequest loginRequest) {
        
        // 1. Intentamos autenticar al usuario con sus credenciales
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.username(), 
                    loginRequest.password()
                )
        );

        // 2. Si llegamos aquí es que las credenciales son válidas. Generamos el token
        String token = jwtTokenProvider.generateToken(authentication);

        // 3. Devolvemos el token en un formato JSON estándar
        Map<String, String> response = new HashMap<>();
        response.put("accessToken", token);
        response.put("tokenType", "Bearer");

        return ResponseEntity.ok(response);
    }
}

/**
 * Clase record (Java 14+) para recibir los datos de login de forma concisa.
 * Si tu versión de Java es antigua, usa una clase normal con getters/setters.
 */
record LoginRequest(String username, String password) {}