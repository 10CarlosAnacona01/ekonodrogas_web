package com.ekonodrogas.ekonodrogas.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

/*
 * DTO que representa una venta completa con todos sus detalles
 * Incluye la información de la venta principal y todos los productos comprados
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO completo de venta con todos los detalles de productos")
public class VentaCompletaDTO {

    @Schema(description = "ID de la venta", example = "1")
    private Long idVenta;

    @Schema(description = "ID del usuario que realizó la compra", example = "5")
    private Long idUsuario;

    @Schema(description = "Nombre completo del usuario")
    private String nombreUsuario;

    @Schema(description = "Fecha y hora de la venta")
    private LocalDateTime fechaVenta;

    @Schema(description = "Total de la venta", example = "50000")
    private Integer totalVenta;

    @Schema(description = "Estado de la venta", example = "completada")
    private String estadoVenta;

    @Schema(description = "Lista de todos los productos comprados en esta venta")
    private List<DetalleProductoDTO> detalles;

    @Schema(description = "Cantidad total de productos diferentes")
    private Integer cantidadProductosDiferentes;

    @Schema(description = "Cantidad total de unidades compradas")
    private Integer cantidadTotalUnidades;


    /* DTO interno para representar cada producto dentro de la venta  */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Detalle de un producto dentro de la venta")
    public static class DetalleProductoDTO {

        @Schema(description = "ID del producto", example = "10")
        private Long idProducto;

        @Schema(description = "Nombre del producto", example = "Paracetamol 500mg")
        private String nombreProducto;

        @Schema(description = "Categoría del producto", example = "Analgésicos")
        private String categoria;

        @Schema(description = "Cantidad comprada", example = "3")
        private Integer cantidad;

        @Schema(description = "Precio unitario", example = "5000")
        private Integer precioUnitario;

        @Schema(description = "Subtotal (cantidad * precio)", example = "15000")
        private Integer subtotal;
    }
}
