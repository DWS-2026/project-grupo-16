package es.codeurjc.ferrumgym.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.ui.Model;
import java.security.Principal;

@ControllerAdvice
public class CSRFHandlerInterceptor {

    @ModelAttribute
    public void addGlobalAttributes(Model model, HttpServletRequest request) {
        // 1. The CSRF token (Existing functionality to protect web forms)
        CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        if (csrfToken != null) {
            model.addAttribute("_csrf", csrfToken);
        }

        // 2. Magic variables to check if the user is logged in and their role (Used in Thymeleaf templates)
        Principal principal = request.getUserPrincipal();
        if (principal != null) {
            model.addAttribute("logged", true);
            model.addAttribute("userName", principal.getName());
            model.addAttribute("admin", request.isUserInRole("ADMIN"));
        } else {
            model.addAttribute("logged", false);
        }
    }
}