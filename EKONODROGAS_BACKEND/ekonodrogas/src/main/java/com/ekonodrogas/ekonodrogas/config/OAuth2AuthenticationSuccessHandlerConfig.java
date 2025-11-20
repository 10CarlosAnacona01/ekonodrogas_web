package com.ekonodrogas.ekonodrogas.config;

import com.ekonodrogas.ekonodrogas.dto.UsuariosDTO;
import com.ekonodrogas.ekonodrogas.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Component
public class OAuth2AuthenticationSuccessHandlerConfig extends SimpleUrlAuthenticationSuccessHandler {

    private final AuthService authService;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    // Constructor con @Lazy para romper el ciclo
    public OAuth2AuthenticationSuccessHandlerConfig(@Lazy AuthService authService) {
        this.authService = authService;
    }

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException {

        try {
            // Obtener información del usuario OAuth2
            OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();

            String email = oauth2User.getAttribute("email");
            String nombre = oauth2User.getAttribute("given_name");
            String apellido = oauth2User.getAttribute("family_name");

            // Procesar login/registro
            UsuariosDTO usuario = authService.procesarLoginGoogle(email, nombre, apellido);
            String token = authService.generarToken(usuario);

            // Redirigir al frontend con el token
            String redirectUrl = frontendUrl + "?token=" + token;
            getRedirectStrategy().sendRedirect(request, response, redirectUrl);

        } catch (Exception e) {
            // En caso de error, redirigir con mensaje de error
            String redirectUrl = frontendUrl + "?error=" + e.getMessage();
            getRedirectStrategy().sendRedirect(request, response, redirectUrl);
        }
    }
}
