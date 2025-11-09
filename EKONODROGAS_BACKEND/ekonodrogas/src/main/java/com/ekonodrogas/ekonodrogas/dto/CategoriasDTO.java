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
@Schema(description = "DTO para la transferencia de datos de categorías")
public class CategoriasDTO {

    @Schema(description = "Id de la categoría", example = "1")
    private Long idCategoria;

    @Schema(description = "Nombre de la categoría", example = "Droguería")
    private String nombreCategoria;
}
