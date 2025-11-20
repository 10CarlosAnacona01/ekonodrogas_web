package com.ekonodrogas.ekonodrogas.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API para la gestión de una droguería - Universidad Surcolombiana")
                        .version("1.0")
                        .description("Documentación de la API para gestionar una droguería, " +
                                "incluyendo operaciones CRUD y manejo de estados. " +
                                "Esta API incluye autenticación JWT y OAuth2 con Google.")
                        .contact(new Contact()
                                .name("Carlos Esteban Anacona Gonzalez")
                                .email("estebananacona13@gmail.com")))
                // Configurar autenticación JWT en Swagger
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new Components()
                        .addSecuritySchemes("Bearer Authentication",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .in(SecurityScheme.In.HEADER)
                                        .name("Authorization")
                                        .description("Ingresa el token JWT obtenido del login. Ejemplo: Bearer eyJhbGc...")
                        )
                );
    }

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("Gestión_de_droguería")
                .packagesToScan("com.ekonodrogas.ekonodrogas.controller")
                .build();
    }

    @Bean
    public GroupedOpenApi authApi() {
        return GroupedOpenApi.builder()
                .group("01-Autenticación")
                .pathsToMatch("/api/auth/**")
                .build();
    }

    @Bean
    public GroupedOpenApi usuariosApi() {
        return GroupedOpenApi.builder()
                .group("02-Usuarios_y_Roles")
                .pathsToMatch("/api/usuarios/**", "/api/roles/**")
                .build();
    }

    @Bean
    public GroupedOpenApi productosApi() {
        return GroupedOpenApi.builder()
                .group("03-Productos_y_Categorías")
                .pathsToMatch("/api/productos/**", "/api/categorias/**", "/api/ofertas/**")
                .build();
    }

    @Bean
    public GroupedOpenApi carritoApi() {
        return GroupedOpenApi.builder()
                .group("04-Carrito_de_Compras")
                .pathsToMatch("/api/carrito/**")
                .build();
    }

    @Bean
    public GroupedOpenApi ventasApi() {
        return GroupedOpenApi.builder()
                .group("05-Ventas_y_Pagos")
                .pathsToMatch("/api/ventas/**", "/api/detalle-ventas/**", "/api/pagos/**")
                .build();
    }
}
