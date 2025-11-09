package com.ekonodrogas.ekonodrogas.repository;


import com.ekonodrogas.ekonodrogas.persistence.UsuariosEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuariosRepository extends JpaRepository<UsuariosEntity, Long> {

}
