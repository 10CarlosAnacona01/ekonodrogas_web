package com.ekonodrogas.ekonodrogas;

import com.ekonodrogas.ekonodrogas.persistence.*;
import com.ekonodrogas.ekonodrogas.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

@SpringBootApplication
public class EkonodrogasApplication {

    public static void main(String[] args) {
        SpringApplication.run(EkonodrogasApplication.class, args);
    }

    @Bean
    public CommandLineRunner dataLoader(
            UsuariosRepository usuariosRepository,
            RolesRepository rolesRepository,
            CategoriasRepository categoriasRepository,
            PasswordEncoder passwordEncoder) {

        return args -> {
            // ROLES
            // Buscar o crear rol ADMINISTRADOR
            RolesEntity adminRole;
            Optional<RolesEntity> adminRoleOpt = rolesRepository.findByNombreRol("ADMINISTRADOR");
            if (adminRoleOpt.isEmpty()) {
                adminRole = RolesEntity.builder()
                        .nombreRol("ADMINISTRADOR")
                        .build();
                adminRole = rolesRepository.save(adminRole);
            } else {
                adminRole = adminRoleOpt.get();
            }

            // Buscar o crear rol USUARIO
            RolesEntity userRole;
            Optional<RolesEntity> userRoleOpt = rolesRepository.findByNombreRol("USUARIO");
            if (userRoleOpt.isEmpty()) {
                userRole = RolesEntity.builder()
                        .nombreRol("USUARIO")
                        .build();
                userRole = rolesRepository.save(userRole);
            } else {
                userRole = userRoleOpt.get();
            }

            // USUARIO ADMINISTRADOR
            if (!usuariosRepository.existsByCorreo("adminekonodrogas@gmail.com")) {
                UsuariosEntity administrador = UsuariosEntity.builder()
                        .roles(Set.of(adminRole))
                        .primerNombre("Carlos")
                        .segundoNombre("Esteban")
                        .primerApellido("Anacona")
                        .segundoApellido("Gonzalez")
                        .correo("adminekonodrogas@gmail.com")
                        .contrasena(passwordEncoder.encode("EKONODROGAS2025."))
                        .fechaRegistro(LocalDateTime.now())
                        .build();

                usuariosRepository.save(administrador);
            }

            // CATEGORÍAS
            if (categoriasRepository.count() == 0) {
                CategoriasEntity categoriaDrogueria = CategoriasEntity.builder()
                        .nombreCategoria("Droguería")
                        .build();
                CategoriasEntity categoriaMaternidad = CategoriasEntity.builder()
                        .nombreCategoria("Maternidad y bebes")
                        .build();
                CategoriasEntity categoriaDermocosmetica = CategoriasEntity.builder()
                        .nombreCategoria("Dermocosmética")
                        .build();
                CategoriasEntity categoriaOferta = CategoriasEntity.builder()
                        .nombreCategoria("Ofertas y descuentos")
                        .build();

                categoriasRepository.saveAll(Set.of(
                        categoriaDrogueria,
                        categoriaMaternidad,
                        categoriaDermocosmetica,
                        categoriaOferta
                ));

            }
        };
    }
}
