package com.ekonodrogas.ekonodrogas.repository;

import com.ekonodrogas.ekonodrogas.persistence.CategoriasEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoriasRepository extends JpaRepository<CategoriasEntity, Long> {
}
