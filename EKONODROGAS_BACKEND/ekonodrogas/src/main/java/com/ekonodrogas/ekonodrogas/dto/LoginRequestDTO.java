package com.ekonodrogas.ekonodrogas.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para solicitud de inicio de sesión")
public class LoginRequestDTO {

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "El correo debe tener un formato válido")
    @Schema(description = "Correo del usuario", example = "adminekonodrogas@gmail.com")
    private String correo;

    @NotBlank(message = "La contraseña es obligatoria")
    @Schema(description = "Contraseña del usuario", example = "EKONODROGAS2025.")
    private String contrasena;
}