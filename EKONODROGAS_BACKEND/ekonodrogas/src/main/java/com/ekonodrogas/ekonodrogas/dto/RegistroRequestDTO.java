package com.ekonodrogas.ekonodrogas.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para solicitud de registro de usuario")
public class RegistroRequestDTO {

    @NotBlank(message = "El primer nombre es obligatorio")
    @Size(max = 30, message = "El primer nombre no puede exceder 30 caracteres")
    @Schema(description = "Primer nombre", example = "Carlos")
    private String primerNombre;

    @Size(max = 30, message = "El segundo nombre no puede exceder 30 caracteres")
    @Schema(description = "Segundo nombre", example = "Esteban")
    private String segundoNombre;

    @NotBlank(message = "El primer apellido es obligatorio")
    @Size(max = 30, message = "El primer apellido no puede exceder 30 caracteres")
    @Schema(description = "Primer apellido", example = "Anacona")
    private String primerApellido;

    @Size(max = 30, message = "El segundo apellido no puede exceder 30 caracteres")
    @Schema(description = "Segundo apellido", example = "Gonzalez")
    private String segundoApellido;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "El correo debe tener un formato válido")
    @Schema(description = "Correo electrónico", example = "usuario@example.com")
    private String correo;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    @Schema(description = "Contraseña", example = "Password123.")
    private String contrasena;
}
