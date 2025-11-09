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
@Table(name = "ofertas")
public class OfertasEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_oferta")
    private Long idOferta;

    @OneToOne
    @JoinColumn(name = "id_producto", nullable = false, unique = true)
    private ProductosEntity idProducto;

    @Column(name = "precio_anterior", nullable = false)
    private Integer precioAnterior;

    @Column(name = "precio_nuevo", nullable = false)
    private Integer precioNuevo;

    @Column(name = "descuento_porcentaje")
    private Integer descuentoPorcentaje;

    @Column(name = "fecha_creacion")
    private java.time.LocalDateTime fechaCreacion;
}
