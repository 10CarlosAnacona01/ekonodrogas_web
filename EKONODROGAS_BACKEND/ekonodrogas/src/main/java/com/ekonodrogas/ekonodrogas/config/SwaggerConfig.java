package com.ekonodrogas.ekonodrogas.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springdoc.core.models.GroupedOpenApi; // Conecta con Spring para conf automática
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
                                "incluyendo operaciones CRUD " +
                                "y manejo de estados")
                        .contact(new Contact()
                                .name("Carlos Esteban Anacona Gonzalez")
                                .email("estebananacona13@gmail.com")));
    }

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("Gestión_de_droguería")
                .packagesToScan("com.ekonodrogas.ekonodrogas.controller")
                .build();
    }
}
