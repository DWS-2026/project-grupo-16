package es.codeurjc.ferrumgym.controller.rest;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

// Catch-all controller for non-existent API routes (e.g., /api/v1/unknown-route)
// This ensures that invalid API URLs return a JSON response instead of an HTML 404 page.
@RestController
public class ApiErrorController {

    // Intercepts any request starting with /api/ that hasn't been mapped elsewhere
    @RequestMapping("/api/**")
    public ResponseEntity<Map<String, Object>> handleApiNotFound(HttpServletRequest request) {
        Map<String, Object> errorJson = new HashMap<>();
        errorJson.put("status", HttpStatus.NOT_FOUND.value());
        errorJson.put("error", HttpStatus.NOT_FOUND.getReasonPhrase());
        errorJson.put("message", "The requested API endpoint does not exist.");
        errorJson.put("path", request.getRequestURI());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorJson);
    }
}

// Global exception handler for the REST API controllers
// This prevents HTML 500 pages from being returned if a REST controller throws an exception.
@RestControllerAdvice(basePackages = "es.codeurjc.ferrumgym.controller.rest")
class ApiExceptionHandler {

    // NUEVO: Interceptor específico para errores de validación en los DTOs
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, Object> errorJson = new HashMap<>();
        errorJson.put("status", HttpStatus.BAD_REQUEST.value());
        errorJson.put("error", "Validation Error");
        
        Map<String, String> fieldErrors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(error.getField(), error.getDefaultMessage());
        }
        
        errorJson.put("message", fieldErrors);
        errorJson.put("path", request.getRequestURI());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorJson);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleAllExceptions(Exception ex, HttpServletRequest request) {
        Map<String, Object> errorJson = new HashMap<>();
        errorJson.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        errorJson.put("error", HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        errorJson.put("message", ex.getMessage());
        errorJson.put("path", request.getRequestURI());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorJson);
    }
}
