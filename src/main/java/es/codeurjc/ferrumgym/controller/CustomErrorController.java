package es.codeurjc.ferrumgym.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.Map;

@Controller
public class CustomErrorController { // ¡Hemos quitado el 'implements ErrorController'!

    @RequestMapping("/error")
    public Object handleError(HttpServletRequest request) {

        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        // Usamos FORWARD_REQUEST_URI para saber la URL original que falló
        String uri = (String) request.getAttribute(RequestDispatcher.FORWARD_REQUEST_URI);

        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());

            // 1. Si el error viene de la API REST, devolvemos JSON
            if (uri != null && uri.startsWith("/api/")) {
                Map<String, Object> errorJson = new HashMap<>();
                errorJson.put("status", statusCode);
                errorJson.put("error", HttpStatus.valueOf(statusCode).getReasonPhrase());
                errorJson.put("path", uri);

                return ResponseEntity.status(statusCode).body(errorJson);
            }

            // 2. Si el error viene de la Web, devolvemos la vista HTML correspondiente
            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                return "error/404";
            } else if (statusCode == HttpStatus.FORBIDDEN.value()) {
                return "error/403";
            }
        }

        // Error genérico para la web
        return "error/500";
    }
}
