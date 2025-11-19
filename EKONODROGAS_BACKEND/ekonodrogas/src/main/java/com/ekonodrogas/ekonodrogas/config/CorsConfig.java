package com.ekonodrogas.ekonodrogas.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/* Configuración de CORS para permitir peticiones del frontend */
@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {

            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")
                        // Permitir peticiones desde estas URLs (ajusta según configuración)
                        .allowedOrigins("http://localhost:5501", "http://127.0.0.1:5501")
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
