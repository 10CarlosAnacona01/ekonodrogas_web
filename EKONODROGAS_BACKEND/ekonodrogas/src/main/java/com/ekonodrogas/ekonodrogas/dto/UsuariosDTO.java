package com.ekonodrogas.ekonodrogas.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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

    @Schema(description = "Id del usuario",example = "1")
    private Long id;

    @Schema(description = "Id del rol",example = "1")
    private Long idRol;

    @Schema(description = "Primer nombre del usuario",example = "Carlos")
    private String primerNombre;

    @Schema(description = "Segundo nombre del usuario",example = "Esteban")
    private String segundoNombre;

    @Schema(description = "Primer apellido del usuario",example = "Anacona")
    private String primerApellido;

    @Schema(description = "Segundo apellido del usuario",example = "Gonzalez")
    private String segundoApellido;

    @Schema(description = "Correo del usuario",example = "anaconacarlos82@gmail.com")
    private String correo;

    @Schema(description = "Fecha de registro del usuario",example = "8/11/2025")
    private LocalDateTime fechaRegistro;

}
