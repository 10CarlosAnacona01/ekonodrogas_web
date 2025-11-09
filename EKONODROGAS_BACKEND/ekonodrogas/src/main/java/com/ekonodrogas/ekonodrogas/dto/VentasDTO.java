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
@Schema(description = "DTO para la transferencia de las ventas")
public class VentasDTO {

    @Schema(description = "Id de la venta",example = "1")
    private Long idVenta;

    @Schema(description = "Id del usuario",example = "1")
    private Long idUsuario;

    @Schema(description = "Fecha de la venta",example = "9/11/2025")
    private LocalDateTime fechaVenta;

    @Schema(description = "Total de la venta",example = "25000")
    private Integer totalVenta;

    @Schema(description = "Como se encuentra la venta",example = "pendiente")
    private String estadoVenta;

}
