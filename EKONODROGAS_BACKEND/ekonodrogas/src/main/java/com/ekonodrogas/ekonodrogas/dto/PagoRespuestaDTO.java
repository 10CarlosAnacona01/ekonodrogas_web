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
@Schema(description = "Respuesta del procesamiento de pago")
public class PagoRespuestaDTO {

    @Schema(description = "ID de la venta generada", example = "1")
    private Long idVenta;

    @Schema(description = "Estado del pago", example = "completada")
    private String estadoPago;

    @Schema(description = "Mensaje de respuesta", example = "Pago procesado exitosamente")
    private String mensaje;

    @Schema(description = "Fecha y hora del pago", example = "2025-11-12T10:30:00")
    private LocalDateTime fechaPago;

    @Schema(description = "Monto total pagado", example = "50000")
    private Integer montoTotal;

    @Schema(description = "Número de autorización ficticio", example = "AUTH-123456789")
    private String numeroAutorizacion;

    @Schema(description = "PDF del comprobante en Base64")
    private String comprobanteBase64;
}
