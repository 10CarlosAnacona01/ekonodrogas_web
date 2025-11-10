package com.ekonodrogas.ekonodrogas.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para la transferencia de datos de detalle de ventas")
public class DetalleVentasDTO {

    @Schema(description = "Id del detalle de venta", example = "1")
    private Long idDetalleVenta;

    @NotNull(message = "La venta es obligatoria")
    @Schema(description = "Id de la venta", example = "1")
    private Long idVenta;

    @NotNull(message = "El producto es obligatorio")
    @Schema(description = "Id del producto", example = "1")
    private Long idProducto; // CORREGIDO: era "productos"

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    @Schema(description = "Cantidad de productos", example = "3")
    private Integer cantidad;

    @NotNull(message = "El precio unitario es obligatorio")
    @Min(value = 0, message = "El precio unitario debe ser mayor o igual a 0")
    @Schema(description = "Precio de cada producto por unidad", example = "1500")
    private Integer precioUnitario;

    @Schema(description = "Precio total de todos los productos", example = "4500")
    private Integer subtotal;
}
