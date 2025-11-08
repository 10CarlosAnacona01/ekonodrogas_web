package com.ekonodrogas.ekonodrogas.service;

import com.ekonodrogas.ekonodrogas.persistence.ClaseEntity;
import com.ekonodrogas.ekonodrogas.repository.ClaseRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service // Acceder a los datos
@Transactional // Si falla se revierte todo
public class ClaseService {
    private final ClaseRepository claseRepository;

    @Autowired
    public ClaseService(ClaseRepository claseRepository) {
        this.claseRepository = claseRepository;
    }

    public ClaseEntity save(ClaseEntity clase) {
        return claseRepository.save(clase);
    }

    public boolean isDocenteOfClase(String username, Long claseId) {
        ClaseEntity clase = claseRepository.findById(claseId)
                .orElse(null);
        if (clase == null) return false;
        return clase.getDocenteUsername().equals(username);
    }

    public List<ClaseEntity> findClasesByDocente(String username) {
        return claseRepository.findByDocenteUsername(username);
    }
}