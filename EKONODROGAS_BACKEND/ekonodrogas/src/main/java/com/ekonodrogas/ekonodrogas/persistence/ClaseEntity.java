package com.ekonodrogas.ekonodrogas.persistence;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor // Constructor vacío
@AllArgsConstructor
@Builder
@Entity
@Table(name = "clases")
public class ClaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    protected String descripcion;
    private Integer horarioInicio;
    private Integer horarioFinal;
    private String docenteUsername;
    private String salon;

}
