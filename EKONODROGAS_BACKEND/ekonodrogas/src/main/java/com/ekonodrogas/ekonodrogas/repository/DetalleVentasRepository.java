package com.ekonodrogas.ekonodrogas.repository;

import com.ekonodrogas.ekonodrogas.persistence.DetalleVentasEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetalleVentasRepository extends JpaRepository<DetalleVentasEntity, Long> {
    List<DetalleVentasEntity> findByVentaIdVenta(Long idVenta);
}
