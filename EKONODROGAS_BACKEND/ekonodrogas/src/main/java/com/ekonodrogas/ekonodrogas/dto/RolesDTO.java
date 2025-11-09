package com.ekonodrogas.ekonodrogas.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para la transferencia de datos de los roles")
public class RolesDTO {

    @Schema(description = "Id del rol", example = "1")
    private Long idRol;

    @NotBlank(message = "El nombre del rol es obligatorio")
    @Schema(description = "Nombre del rol", example = "Admin")
    private String nombreRol;
}