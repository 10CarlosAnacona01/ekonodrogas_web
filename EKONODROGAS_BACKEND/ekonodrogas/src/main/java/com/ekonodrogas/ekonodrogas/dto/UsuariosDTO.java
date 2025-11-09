package com.ekonodrogas.ekonodrogas.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para la transferencia de datos del usuario")
public class UsuariosDTO {

    @Schema(description = "Id del usuario", example = "1")
    private Long idUsuario;

    @NotNull(message = "El rol es obligatorio")
    @Schema(description = "Id del rol", example = "1")
    private Long idRol;

    @NotBlank(message = "El primer nombre es obligatorio")
    @Size(max = 30, message = "El primer nombre no puede exceder 30 caracteres")
    @Schema(description = "Primer nombre del usuario", example = "Carlos")
    private String primerNombre;

    @Size(max = 30, message = "El segundo nombre no puede exceder 30 caracteres")
    @Schema(description = "Segundo nombre del usuario", example = "Esteban")
    private String segundoNombre;

    @NotBlank(message = "El primer apellido es obligatorio")
    @Size(max = 30, message = "El primer apellido no puede exceder 30 caracteres")
    @Schema(description = "Primer apellido del usuario", example = "Anacona")
    private String primerApellido;

    @Size(max = 30, message = "El segundo apellido no puede exceder 30 caracteres")
    @Schema(description = "Segundo apellido del usuario", example = "Gonzalez")
    private String segundoApellido;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "El correo debe tener un formato válido")
    @Schema(description = "Correo del usuario", example = "anaconacarlos82@gmail.com")
    private String correo;

    // Mirar si después registrar contraseña

    @Schema(description = "Fecha de registro del usuario", example = "2025-11-08T10:30:00")
    private LocalDateTime fechaRegistro;
}
