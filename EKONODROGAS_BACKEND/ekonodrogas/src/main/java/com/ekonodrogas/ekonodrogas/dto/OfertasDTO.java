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
@Schema(description = "DTO para la transferencia de datos de las ofertas")
public class OfertasDTO {

    @Schema(description = "Id de la oferta" ,example = "1")
    private Long idOferta;

    @Schema(description = "Id del producto" ,example = "1")
    private Long idProducto;

    @Schema(description = "Precio anterior del producto" ,example = "5000")
    private Integer precioAnterior;

    @Schema(description = "Precio nuevo de producto" ,example = "4000")
    private Integer precioNuevo;

    @Schema(description = "Porcentaje de descuento de producto" ,example = "20%")
    private Integer descuentoPorcentaje;

    @Schema(description = "Fecha de creación de la oferta" ,example = "08/11/2025")
    private LocalDateTime fechaCreacion;
}
