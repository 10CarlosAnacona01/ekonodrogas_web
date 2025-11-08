package com.ekonodrogas.ekonodrogas.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;


@Tag(name = "Home", description = "API para la página principal")
@Controller
@RequestMapping("/home")
public class HomeController {

    @Operation(summary = "Mostrar página principal",
            description = "Muestra la página principal del sistema con las opciones disponibles según el rol del usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Página principal mostrada con éxito"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    @GetMapping
    public String home() {
        return "home";
    }

}
