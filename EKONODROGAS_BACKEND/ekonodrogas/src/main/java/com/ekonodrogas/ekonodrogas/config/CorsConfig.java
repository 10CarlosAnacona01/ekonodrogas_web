package com.ekonodrogas.ekonodrogas.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/*
 * Configuración de CORS para permitir peticiones del frontend
 * IMPORTANTE: Asegúrate de que esta clase esté en el paquete config
 */
@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")
                        // Permitir peticiones desde estas URLs (ajusta según tu configuración)
                        .allowedOrigins(
                                "http://localhost:5500",      // Live Server (Visual Studio Code)
                                "http://127.0.0.1:5500",     // Live Server alternativo
                                "http://localhost:3000",      // React dev server
                                "http://localhost:8081"       // Otro puerto común
                        )
                        // Métodos HTTP permitidos
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        // Permitir todos los headers
                        .allowedHeaders("*")
                        // Permitir cookies/credenciales
                        .allowCredentials(true)
                        // Tiempo máximo que el navegador puede cachear la respuesta preflight
                        .maxAge(3600);
            }
        };
    }
}

/*
 * NOTAS IMPORTANTES:
 *
 * 1. Si usas Live Server de VS Code, normalmente corre en http://localhost:5500
 * 2. Verifica el puerto exacto en la barra de direcciones de tu navegador
 * 3. Si cambias de puerto, agrega la URL correspondiente en allowedOrigins
 * 4. Para producción, reemplaza con la URL real de tu frontend
 *
 * EJEMPLO DE VERIFICACIÓN:
 * - Abre el panel en el navegador
 * - Mira la URL (ej: http://localhost:5500/panel_admin.html)
 * - Asegúrate de que esa URL esté en allowedOrigins
 */
