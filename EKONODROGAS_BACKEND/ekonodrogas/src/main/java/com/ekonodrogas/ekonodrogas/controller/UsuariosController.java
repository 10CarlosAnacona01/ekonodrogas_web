package com.ekonodrogas.ekonodrogas.controller;

import com.ekonodrogas.ekonodrogas.dto.UsuariosDTO;
import com.ekonodrogas.ekonodrogas.service.UsuariosService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
@Tag(name = "Usuarios", description = "API para gestión de usuarios del sistema")
public class UsuariosController {

    private final UsuariosService usuariosService;

    @GetMapping
    @Operation(summary = "Obtener todos los usuarios", description = "Retorna una lista con todos los usuarios registrados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de usuarios obtenida exitosamente"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<List<UsuariosDTO>> obtenerTodos() {
        return ResponseEntity.ok(usuariosService.obtenerTodos());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener usuario por ID", description = "Retorna un usuario específico por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<UsuariosDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(usuariosService.obtenerPorId(id));
    }

    @PostMapping
    @Operation(summary = "Registrar nuevo usuario", description = "Crea un nuevo usuario en el sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<UsuariosDTO> crear(@RequestBody Map<String, Object> request) {
        // Se espera: { "usuario": {...}, "contrasena": "..." }
        UsuariosDTO dto = mapToUsuarioDTO(request);
        String contrasena = (String) request.get("contrasena");

        // IMPORTANTE: La contraseña debería encriptarse aquí con BCrypt antes de enviarla al servicio
        return ResponseEntity.status(HttpStatus.CREATED).body(usuariosService.crear(dto, contrasena));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar usuario", description = "Actualiza los datos de un usuario existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario actualizado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<UsuariosDTO> actualizar(@PathVariable Long id, @RequestBody UsuariosDTO dto) {
        return ResponseEntity.ok(usuariosService.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar usuario", description = "Elimina un usuario del sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Usuario eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        usuariosService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // Método auxiliar para mapear el request
    private UsuariosDTO mapToUsuarioDTO(Map<String, Object> request) {
        Map<String, Object> usuarioMap = (Map<String, Object>) request.get("usuario");
        return UsuariosDTO.builder()
                .idRol(((Number) usuarioMap.get("idRol")).longValue())
                .primerNombre((String) usuarioMap.get("primerNombre"))
                .segundoNombre((String) usuarioMap.get("segundoNombre"))
                .primerApellido((String) usuarioMap.get("primerApellido"))
                .segundoApellido((String) usuarioMap.get("segundoApellido"))
                .correo((String) usuarioMap.get("correo"))
                .build();
    }
}