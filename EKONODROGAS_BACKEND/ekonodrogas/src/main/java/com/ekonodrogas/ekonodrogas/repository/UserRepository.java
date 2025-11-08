package com.ekonodrogas.ekonodrogas.repository;


import com.ekonodrogas.ekonodrogas.persistence.UsuariosEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UsuariosEntity, Long> {
    Optional<UsuariosEntity> findByUsername(String username);
}
