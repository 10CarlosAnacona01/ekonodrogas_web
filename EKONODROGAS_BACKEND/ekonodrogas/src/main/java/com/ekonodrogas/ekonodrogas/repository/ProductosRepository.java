package com.ekonodrogas.ekonodrogas.repository;

import com.ekonodrogas.ekonodrogas.persistence.ProductosEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductosRepository extends JpaRepository<ProductosEntity,Long> {
    List<ProductosEntity> findByCategoriaIdCategoria(Long categoriaId);
}
