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
@Schema(description = "DTO para procesar pagos")
public class PagoDTO {

    @NotNull(message = "El usuario es obligatorio")
    @Schema(description = "ID del usuario que realiza el pago", example = "1")
    private Long idUsuario;

    @NotBlank(message = "El nombre del titular es obligatorio")
    @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
    @Schema(description = "Nombre completo del titular de la tarjeta", example = "Juan Pérez")
    private String nombreTitular;

    @NotBlank(message = "El número de tarjeta es obligatorio")
    @Pattern(regexp = "^[0-9]{16}$", message = "El número de tarjeta debe tener 16 dígitos")
    @Schema(description = "Número de tarjeta", example = "4532123456789012")
    private String numeroTarjeta;

    @NotBlank(message = "La fecha de vencimiento es obligatoria")
    @Pattern(regexp = "^(0[1-9]|1[0-2])\\/[0-9]{2}$", message = "Formato debe ser MM/YY")
    @Schema(description = "Fecha de vencimiento", example = "12/25")
    private String fechaVencimiento;

    @NotBlank(message = "El CVV es obligatorio")
    @Pattern(regexp = "^[0-9]{3,4}$", message = "El CVV debe tener 3 o 4 dígitos")
    @Schema(description = "Código de seguridad", example = "123")
    private String cvv;

    @NotNull(message = "El monto total es obligatorio")
    @Min(value = 1, message = "El monto debe ser mayor a 0")
    @Schema(description = "Monto total a pagar", example = "50000")
    private Integer montoTotal;
}
