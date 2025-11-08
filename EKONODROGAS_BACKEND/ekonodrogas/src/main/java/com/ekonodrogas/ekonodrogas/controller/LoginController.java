package com.ekonodrogas.ekonodrogas.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Authentication", description = "API para la autenticación de usuarios")
@Controller
public class LoginController {

    @Operation(summary = "Mostrar página de login",
            description = "Muestra el formulario de inicio de sesión")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Formulario de login mostrado con éxito"),
            @ApiResponse(responseCode = "400", description = "Error en los datos de login"),
            @ApiResponse(responseCode = "302", description = "Redirección a página principal si ya está autenticado")
    })
    @GetMapping("/login")
    public String login(){
        return "login";
    }
}
