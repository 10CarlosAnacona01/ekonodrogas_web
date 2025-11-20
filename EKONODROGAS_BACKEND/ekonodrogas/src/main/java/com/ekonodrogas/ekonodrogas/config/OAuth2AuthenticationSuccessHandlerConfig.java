package com.ekonodrogas.ekonodrogas.config;

import com.ekonodrogas.ekonodrogas.dto.UsuariosDTO;
import com.ekonodrogas.ekonodrogas.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class OAuth2AuthenticationSuccessHandlerConfig extends SimpleUrlAuthenticationSuccessHandler {

    private static final Logger logger = LoggerFactory.getLogger(OAuth2AuthenticationSuccessHandlerConfig.class);
    private final AuthService authService;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    public OAuth2AuthenticationSuccessHandlerConfig(@Lazy AuthService authService) {
        this.authService = authService;
    }

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException {

        try {
            OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();

            // Log de los atributos recibidos de Google
            logger.info("Atributos OAuth2: {}", oauth2User.getAttributes());

            String email = oauth2User.getAttribute("email");
            String nombre = oauth2User.getAttribute("given_name");
            String apellido = oauth2User.getAttribute("family_name");

            // Validación del email
            if (email == null || email.isEmpty()) {
                throw new RuntimeException("No se pudo obtener el email de Google");
            }

            logger.info("Procesando login para: {}", email);

            // Procesar login/registro
            UsuariosDTO usuario = authService.procesarLoginGoogle(email, nombre, apellido);
            String token = authService.generarToken(usuario);

            logger.info("Token generado exitosamente para: {}", email);

            // Redirigir al frontend con el token
            String redirectUrl = frontendUrl + "?token=" + URLEncoder.encode(token, StandardCharsets.UTF_8);
            logger.info("Redirigiendo a: {}", redirectUrl);

            getRedirectStrategy().sendRedirect(request, response, redirectUrl);

        } catch (Exception e) {
            logger.error("Error en OAuth2 callback: ", e);

            // Redirigir con mensaje de error
            String errorMsg = URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8);
            String redirectUrl = frontendUrl + "?error=" + errorMsg;
            getRedirectStrategy().sendRedirect(request, response, redirectUrl);
        }
    }
}