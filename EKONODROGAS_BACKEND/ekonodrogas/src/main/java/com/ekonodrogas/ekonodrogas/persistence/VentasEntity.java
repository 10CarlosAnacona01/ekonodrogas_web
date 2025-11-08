package com.ekonodrogas.ekonodrogas.persistence;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "ventas")
public class VentasEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_venta")
    private Long idVenta;

    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private UserEntity usuario;

    @Column(name = "fecha_venta")
    private java.time.LocalDateTime fechaVenta;

    @Column(name = "total_venta")
    private Integer totalVenta;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_venta")
    private EstadoVenta estadoVenta;

    public enum EstadoVenta {
        completada, cancelada, pendiente
    }

}
