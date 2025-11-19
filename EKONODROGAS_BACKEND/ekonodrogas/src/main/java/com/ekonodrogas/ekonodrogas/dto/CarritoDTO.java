package com.ekonodrogas.ekonodrogas.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para el carrito de compras")
public class CarritoDTO {

    @Schema(description = "ID del usuario", example = "1")
    private Long idUsuario;

    @Schema(description = "Lista de items en el carrito")
    @Builder.Default
    private List<ItemCarritoDTO> items = new ArrayList<>();

    @Schema(description = "Total del carrito", example = "25000")
    private Integer total;

    @Schema(description = "Cantidad total de productos", example = "5")
    private Integer cantidadTotal;
}
