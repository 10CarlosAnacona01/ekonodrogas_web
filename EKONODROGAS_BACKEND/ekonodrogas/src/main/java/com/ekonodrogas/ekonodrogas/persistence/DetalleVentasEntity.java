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
@Table(name = "detalle_ventas")
public class DetalleVentasEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle_venta")
    private Long idDetalleVenta;

    @ManyToOne
    @JoinColumn(name = "id_venta", nullable = false)
    private VentasEntity venta;

    @ManyToOne
    @JoinColumn(name = "id_producto", nullable = false)
    private ProductosEntity producto;

    private Integer cantidad;

    @Column(name = "precio_unitario")
    private Integer precioUnitario;
    private Integer subtotal;
}
