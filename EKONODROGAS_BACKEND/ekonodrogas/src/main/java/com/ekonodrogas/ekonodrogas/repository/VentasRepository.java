package com.ekonodrogas.ekonodrogas.repository;

import com.ekonodrogas.ekonodrogas.persistence.VentasEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VentasRepository extends JpaRepository<VentasEntity,Long> {
}
