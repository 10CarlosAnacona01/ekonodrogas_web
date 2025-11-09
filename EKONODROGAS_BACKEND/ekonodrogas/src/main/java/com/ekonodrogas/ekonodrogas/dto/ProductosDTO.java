package com.ekonodrogas.ekonodrogas.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
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

    @Schema(description = "Id del producto", example = "1")
    private Long idProducto;

    @NotNull(message = "La categoría es obligatoria")
    @Schema(description = "Id de la categoría", example = "1")
    private Long idCategoria;

    @NotBlank(message = "El nombre del producto es obligatorio")
    @Size(max = 40, message = "El nombre no puede exceder 40 caracteres")
    @Schema(description = "Nombre del producto", example = "Neumuc")
    private String nombreProducto;

    @Schema(description = "Imagen del producto", example = "neumic.png")
    private String imagen;

    @NotNull(message = "El precio es obligatorio")
    @Min(value = 0, message = "El precio debe ser mayor o igual a 0")
    @Schema(description = "Precio del producto", example = "5000")
    private Integer precio;

    @NotNull(message = "El stock es obligatorio")
    @Min(value = 0, message = "El stock debe ser mayor o igual a 0")
    @Schema(description = "Inventario del producto", example = "20")
    private Integer stock;

    @Schema(description = "Disponibilidad del producto", example = "true")
    private Boolean disponible;

    @Schema(description = "Fecha de creación del producto", example = "2025-11-08T10:30:00")
    private LocalDateTime fechaCreacion;

    @Schema(description = "Fecha de actualización del producto", example = "2025-11-09T15:45:00")
    private LocalDateTime fechaActualizacion;
}
