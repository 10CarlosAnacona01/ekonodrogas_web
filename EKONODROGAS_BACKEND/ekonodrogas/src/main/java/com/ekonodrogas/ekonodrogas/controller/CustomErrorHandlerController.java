package com.ekonodrogas.ekonodrogas.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Controller
@Tag(name = "Error Handler", description = "API para el manejo de errores del sistema")
public class CustomErrorHandlerController implements ErrorController {

    @Operation(summary = "Manejar errores del sistema",
            description = "Maneja diferentes tipos de errores y muestra páginas personalizadas según el código de error")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "Error de solicitud incorrecta"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado"),
            @ApiResponse(responseCode = "404", description = "Recurso no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        String errorTitle = "Algo salió mal";
        String errorMessage = "Ha ocurrido un error";
        String buttonText = "Volver al Inicio";
        String buttonUrl = "/home";

        if (status != null) {
            Integer statusCode = Integer.valueOf(status.toString());

            switch(statusCode) {
                case 404:
                    errorTitle = "Página no encontrada";
                    errorMessage = "La página que estás buscando no existe";
                    break;

                case 403:
                    errorTitle = "Acceso Denegado";
                    errorMessage = "No tienes permisos para acceder a esta página";
                    break;

                case 500:
                    errorTitle = "Error del Servidor";
                    errorMessage = "Ha ocurrido un error en el servidor.";
                    break;

                case 401:
                    errorTitle = "No Autorizado";
                    errorMessage = "Inicia sesión para acceder";
                    buttonText = "Iniciar Sesión";
                    buttonUrl = "/login";
                    break;
            }
        }

        model.addAttribute("errorCode", status);
        model.addAttribute("errorTitle", errorTitle);
        model.addAttribute("errorMessage", errorMessage);
        model.addAttribute("buttonText", buttonText);
        model.addAttribute("buttonUrl", buttonUrl);

        return "error";
    }
}