package com.ekonodrogas.ekonodrogas.repository;


import com.ekonodrogas.ekonodrogas.persistence.RolesEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<RolesEntity, Long> {
}
