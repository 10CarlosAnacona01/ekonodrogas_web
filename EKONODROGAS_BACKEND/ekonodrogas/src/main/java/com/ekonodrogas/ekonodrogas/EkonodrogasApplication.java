package com.ekonodrogas.ekonodrogas;

import com.ekonodrogas.ekonodrogas.persistence.ProductosEntity;
import com.ekonodrogas.ekonodrogas.persistence.RolesEntity;
import com.ekonodrogas.ekonodrogas.persistence.UsuariosEntity;
import com.ekonodrogas.ekonodrogas.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import java.util.Set;

@SpringBootApplication
public class EkonodrogasApplication {

    public static void main(String[] args) {
        SpringApplication.run(EkonodrogasApplication.class, args);
    }

    @Bean
    public CommandLineRunner dataLoder(UsuariosRepository usuariosRepository, RolesRepository rolesRepository,
                                       ProductosRepository  productosRepository, OfertasRepository  ofertasRepository,
                                       CategoriasRepository categoriasRepository) {
        return args -> {

            // Roles

            RolesEntity adminRole = RolesEntity.builder()
                    .nombreRol("ADMINISTRADOR")
                    .build();

            RolesEntity userRole = RolesEntity.builder()
                    .nombreRol("USUARIO")
                    .build();


            UsuariosRepository.saveAll(Set.of(adminRole,userRole));

        };
    }
}
