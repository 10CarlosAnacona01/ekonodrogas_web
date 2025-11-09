package com.ekonodrogas.ekonodrogas;

import com.ekonodrogas.ekonodrogas.persistence.*;
import com.ekonodrogas.ekonodrogas.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.time.LocalDateTime;
import java.util.Set;

@SpringBootApplication
public class EkonodrogasApplication {

    public static void main(String[] args) {
        SpringApplication.run(EkonodrogasApplication.class, args);
    }

    @Bean
    public CommandLineRunner dataLoder(UsuariosRepository usuariosRepository, RolesRepository rolesRepository,
                                       CategoriasRepository categoriasRepository, PasswordEncoder passwordEncoder) {
        return args -> {

            // Roles

            RolesEntity adminRole = RolesEntity.builder()
                    .nombreRol("ADMINISTRADOR")
                    .build();
            RolesEntity userRole = RolesEntity.builder()
                    .nombreRol("USUARIO")
                    .build();
            rolesRepository.saveAll(Set.of(adminRole, userRole));

            // Usuario admin, verificando que el correo no exista antes de crear

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

                // Categorías

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

                categoriasRepository.saveAll(Set.of(categoriaDrogueria, categoriaMaternidad,
                        categoriaDermocosmetica, categoriaOferta));

            }
            ;
        };
    }
}
