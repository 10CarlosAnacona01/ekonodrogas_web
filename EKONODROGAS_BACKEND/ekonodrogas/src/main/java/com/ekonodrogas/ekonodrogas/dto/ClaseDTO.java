package com.ekonodrogas.ekonodrogas.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data //lombok, genera código repetitivo (getters, setters, constructores, etc.)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para la transferencia de datos de clase")
public class ClaseDTO {

    @Schema(description = "Nombre de la clase", example = "Física")
    private String nombre;

    @Schema(description = "Descripción de la clase", example = "Llevar hoja milimetrada")
    private String descripcion;

    @Schema(description = "Hora de inicio de la clase (formato 24h)", example = "12")
    private Integer horarioInicio;

    @Schema(description = "Hora de finalización de la clase (formato 24h)", example = "15")
    private Integer horarioFinal;

    @Schema(description = "Nombre del docente", example = "Carlos Esteban Anacona Gonzalez")
    private String docenteUsername;

    @Schema(description = "Identificador del salón", example = "603")
    private String salon;

}
