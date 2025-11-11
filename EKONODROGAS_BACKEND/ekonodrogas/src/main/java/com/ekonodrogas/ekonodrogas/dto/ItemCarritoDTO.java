package com.ekonodrogas.ekonodrogas.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para representar un item individual dentro del carrito de compras
 * Contiene toda la información necesaria de un producto en el carrito
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para un item del carrito")
public class ItemCarritoDTO {

    // ID del producto en la base de datos
    @NotNull(message = "El ID del producto es obligatorio")
    @Schema(description = "ID del producto", example = "1")
    private Long idProducto;

    // Nombre del producto (se obtiene del ProductosEntity)
    @Schema(description = "Nombre del producto", example = "Acetaminofén 500mg")
    private String nombreProducto;

    // Ruta de la imagen del producto
    @Schema(description = "Imagen del producto", example = "acetaminofen.png")
    private String imagen;

    // Cantidad de unidades de este producto en el carrito
    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    @Schema(description = "Cantidad de productos", example = "2")
    private Integer cantidad;

    // Precio por unidad del producto
    @NotNull(message = "El precio unitario es obligatorio")
    @Schema(description = "Precio unitario del producto", example = "5000")
    private Integer precioUnitario;

    // Subtotal calculado (cantidad * precioUnitario)
    @Schema(description = "Subtotal del item", example = "10000")
    private Integer subtotal;

    // Stock disponible para validar antes de agregar más unidades
    @Schema(description = "Stock disponible del producto", example = "50")
    private Integer stockDisponible;
}
