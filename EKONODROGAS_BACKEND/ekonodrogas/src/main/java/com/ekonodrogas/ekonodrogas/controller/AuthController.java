package com.ekonodrogas.ekonodrogas.controller;

import com.ekonodrogas.ekonodrogas.dto.AuthResponseDTO;
import com.ekonodrogas.ekonodrogas.dto.LoginRequestDTO;
import com.ekonodrogas.ekonodrogas.dto.RegistroRequestDTO;
import com.ekonodrogas.ekonodrogas.dto.UsuariosDTO;
import com.ekonodrogas.ekonodrogas.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "API para autenticación de usuarios (Login, Registro, OAuth2)")
public class AuthController {

    private final AuthService authService;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    @PostMapping("/registro")
    @Operation(summary = "Registrar nuevo usuario",
            description = "Crea una nueva cuenta de usuario en el sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuario registrado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o correo ya registrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<AuthResponseDTO> registrar(@Valid @RequestBody RegistroRequestDTO request) {
        try {
            UsuariosDTO usuario = authService.registrarUsuario(request);
            String token = authService.generarToken(usuario);

            AuthResponseDTO response = AuthResponseDTO.builder()
                    .token(token)
                    .type("Bearer")
                    .usuario(usuario)
                    .mensaje("Usuario registrado exitosamente")
                    .build();

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            throw new RuntimeException("Error al registrar usuario: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión",
            description = "Autentica un usuario con correo y contraseña")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login exitoso"),
            @ApiResponse(responseCode = "401", description = "Credenciales inválidas"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        try {
            UsuariosDTO usuario = authService.autenticarUsuario(request);
            String token = authService.generarToken(usuario);

            AuthResponseDTO response = AuthResponseDTO.builder()
                    .token(token)
                    .type("Bearer")
                    .usuario(usuario)
                    .mensaje("Login exitoso")
                    .build();

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            throw new RuntimeException("Error al iniciar sesión: " + e.getMessage());
        }
    }

    @GetMapping("/validar")
    @Operation(summary = "Validar token JWT",
            description = "Verifica si un token JWT es válido y retorna información del usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token válido"),
            @ApiResponse(responseCode = "401", description = "Token inválido o expirado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<Map<String, Object>> validarToken(@RequestHeader("Authorization") String authHeader) {
        try {
            // Extraer el token del header (formato: "Bearer <token>")
            String token = authHeader.substring(7);
            // Aquí puedes agregar lógica adicional de validación si lo necesitas

            return ResponseEntity.ok(Map.of(
                    "valido", true,
                    "mensaje", "Token válido"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "valido", false,
                    "mensaje", "Token inválido o expirado"
            ));
        }
    }
}
