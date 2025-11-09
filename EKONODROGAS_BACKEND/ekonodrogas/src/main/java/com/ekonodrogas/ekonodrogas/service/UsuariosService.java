package com.ekonodrogas.ekonodrogas.service;

import com.ekonodrogas.ekonodrogas.dto.UsuariosDTO;
import com.ekonodrogas.ekonodrogas.persistence.RolesEntity;
import com.ekonodrogas.ekonodrogas.persistence.UsuariosEntity;
import com.ekonodrogas.ekonodrogas.repository.RolesRepository;
import com.ekonodrogas.ekonodrogas.repository.UsuariosRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuariosService {

    private final UsuariosRepository usuariosRepository;
    private final RolesRepository rolesRepository;

    @Transactional(readOnly = true)
    public List<UsuariosDTO> obtenerTodos() {
        return usuariosRepository.findAll().stream()
                .map(this::entityToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UsuariosDTO obtenerPorId(Long id) {
        UsuariosEntity entity = usuariosRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
        return entityToDto(entity);
    }

    @Transactional
    public UsuariosDTO crear(UsuariosDTO dto, String contrasena) {
        // Validar que el correo no exista
        // Se recomienda agregar un método en el repository: Optional<UsuariosEntity> findByCorreo(String correo)

        RolesEntity rol = rolesRepository.findById(dto.getIdRol())
                .orElseThrow(() -> new RuntimeException("Rol no encontrado con ID: " + dto.getIdRol()));

        Set<RolesEntity> roles = new HashSet<>();
        roles.add(rol);

        UsuariosEntity entity = UsuariosEntity.builder()
                .roles(roles)
                .primerNombre(dto.getPrimerNombre())
                .segundoNombre(dto.getSegundoNombre())
                .primerApellido(dto.getPrimerApellido())
                .segundoApellido(dto.getSegundoApellido())
                .correo(dto.getCorreo())
                .contrasena(contrasena) // IMPORTANTE: Debería estar encriptada con BCrypt
                .fechaRegistro(LocalDateTime.now())
                .build();

        UsuariosEntity guardado = usuariosRepository.save(entity);
        return entityToDto(guardado);
    }

    @Transactional
    public UsuariosDTO actualizar(Long id, UsuariosDTO dto) {
        UsuariosEntity entity = usuariosRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));

        if (dto.getIdRol() != null) {
            RolesEntity rol = rolesRepository.findById(dto.getIdRol())
                    .orElseThrow(() -> new RuntimeException("Rol no encontrado con ID: " + dto.getIdRol()));
            Set<RolesEntity> roles = new HashSet<>();
            roles.add(rol);
            entity.setRoles(roles);
        }

        entity.setPrimerNombre(dto.getPrimerNombre());
        entity.setSegundoNombre(dto.getSegundoNombre());
        entity.setPrimerApellido(dto.getPrimerApellido());
        entity.setSegundoApellido(dto.getSegundoApellido());
        entity.setCorreo(dto.getCorreo());

        UsuariosEntity actualizado = usuariosRepository.save(entity);
        return entityToDto(actualizado);
    }

    @Transactional
    public void eliminar(Long id) {
        if (!usuariosRepository.existsById(id)) {
            throw new RuntimeException("Usuario no encontrado con ID: " + id);
        }
        usuariosRepository.deleteById(id);
    }

    // Conversión Entity <-> DTO
    private UsuariosDTO entityToDto(UsuariosEntity entity) {
        Long idRol = entity.getRoles().isEmpty() ? null :
                entity.getRoles().iterator().next().getIdRol();

        return UsuariosDTO.builder()
                .idUsuario(entity.getIdUsuario())
                .idRol(idRol)
                .primerNombre(entity.getPrimerNombre())
                .segundoNombre(entity.getSegundoNombre())
                .primerApellido(entity.getPrimerApellido())
                .segundoApellido(entity.getSegundoApellido())
                .correo(entity.getCorreo())
                .fechaRegistro(entity.getFechaRegistro())
                .build();
    }
}