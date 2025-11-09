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
@Schema(description = "DTO para la transferencia de datos de los productos")
public class ProductosDTO {

    @Schema(description = "Id del producto",example = "1")
    private Long idProducto;

    @Schema(description = "Id de la categoría",example = "1")
    private Long idCategoria;

    @Schema(description = "Nombre del producto",example = "Neumuc")
    private String nombreProducto;

    @Schema(description = "Imagen del producto",example = "neumic.png")
    private String imagen;

    @Schema(description = "Precio del producto",example = "5000")
    private Integer precio;

    @Schema(description = "Inventario del producto",example = "20")
    private Integer stock;

    @Schema(description = "Disponibilidad del producto",example = "1")
    private Boolean disponible;

    @Schema(description = "Fecha de creación del producto",example = "8/11/2025")
    private LocalDateTime fechaCreacion;

    @Schema(description = "Fecha de actualización del producto",example = "9/11/2025")
    private LocalDateTime fechaActualizacion;

}
