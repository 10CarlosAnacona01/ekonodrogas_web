package com.ekonodrogas.ekonodrogas.repository;

import com.ekonodrogas.ekonodrogas.persistence.ClaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ClaseRepository extends JpaRepository<ClaseEntity, Long> {
    List<ClaseEntity> findByDocenteUsername(String username);
}
