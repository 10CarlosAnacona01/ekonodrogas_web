package com.ekonodrogas.ekonodrogas.repository;


import com.ekonodrogas.ekonodrogas.persistence.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByUsername(String username);
}
