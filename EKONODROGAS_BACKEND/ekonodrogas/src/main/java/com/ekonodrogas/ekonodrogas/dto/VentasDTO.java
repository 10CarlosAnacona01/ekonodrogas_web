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
@Schema(description = "DTO para la transferencia de las ventas")
public class VentasDTO {

    @Schema(description = "Id de la venta", example = "1")
    private Long idVenta;

    @NotNull(message = "El usuario es obligatorio")
    @Schema(description = "Id del usuario", example = "1")
    private Long idUsuario;

    @Schema(description = "Fecha de la venta", example = "2025-11-09T15:45:00")
    private LocalDateTime fechaVenta;

    @NotNull(message = "El total de la venta es obligatorio")
    @Min(value = 0, message = "El total debe ser mayor o igual a 0")
    @Schema(description = "Total de la venta", example = "25000")
    private Integer totalVenta;

    @NotBlank(message = "El estado de la venta es obligatorio")
    @Pattern(regexp = "completada|cancelada|pendiente", message = "Estado debe ser: completada, cancelada o pendiente")
    @Schema(description = "Como se encuentra la venta", example = "pendiente")
    private String estadoVenta;
}
