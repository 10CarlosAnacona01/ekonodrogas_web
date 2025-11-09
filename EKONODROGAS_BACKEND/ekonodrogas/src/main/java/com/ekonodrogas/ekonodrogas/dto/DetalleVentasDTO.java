package com.ekonodrogas.ekonodrogas.dto;

import com.ekonodrogas.ekonodrogas.persistence.ProductosEntity;
import com.ekonodrogas.ekonodrogas.persistence.VentasEntity;
import io.swagger.v3.oas.annotations.media.Schema;
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

    @Schema(description = "Id de la venta", example = "1")
    private Long idVenta;

    @Schema(description = "Id del producto", example = "1")
    private Long productos;

    @Schema(description = "Cantidad de productos", example = "3")
    private Integer cantidad;

    @Schema(description = "Precio de cada productor por unidad", example = "1500")
    private Integer precioUnitario;

    @Schema(description = "Precio total de todos los productos", example = "4500")
    private Integer subtotal;

}
