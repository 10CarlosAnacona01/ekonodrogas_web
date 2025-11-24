package com.ekonodrogas.ekonodrogas.service;

import com.ekonodrogas.ekonodrogas.dto.LoginRequestDTO;
import com.ekonodrogas.ekonodrogas.dto.RegistroRequestDTO;
import com.ekonodrogas.ekonodrogas.dto.UsuariosDTO;
import com.ekonodrogas.ekonodrogas.persistence.RolesEntity;
import com.ekonodrogas.ekonodrogas.persistence.UsuariosEntity;
import com.ekonodrogas.ekonodrogas.repository.RolesRepository;
import com.ekonodrogas.ekonodrogas.repository.UsuariosRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuariosRepository usuariosRepository;
    private final RolesRepository rolesRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    /*
     * Registra un nuevo usuario en el sistema
     */
    @Transactional
    public UsuariosDTO registrarUsuario(RegistroRequestDTO request) {
        // Validar que el correo no exista
        if (usuariosRepository.existsByCorreo(request.getCorreo())) {
            throw new RuntimeException("El correo ya está registrado");
        }

        // Obtener el rol de USUARIO por defecto
        RolesEntity rolUsuario = rolesRepository.findByNombreRol("USUARIO")
                .orElseThrow(() -> new RuntimeException("Rol USUARIO no encontrado"));

        Set<RolesEntity> roles = new HashSet<>();
        roles.add(rolUsuario);

        // Crear la entidad usuario
        UsuariosEntity nuevoUsuario = UsuariosEntity.builder()
                .roles(roles)
                .primerNombre(request.getPrimerNombre())
                .segundoNombre(request.getSegundoNombre())
                .primerApellido(request.getPrimerApellido())
                .segundoApellido(request.getSegundoApellido())
                .correo(request.getCorreo())
                .contrasena(passwordEncoder.encode(request.getContrasena()))
                .fechaRegistro(LocalDateTime.now())
                .build();

        UsuariosEntity guardado = usuariosRepository.save(nuevoUsuario);
        return entityToDto(guardado);
    }

    /*
     * Autentica un usuario con credenciales
     */
    @Transactional(readOnly = true)
    public UsuariosDTO autenticarUsuario(LoginRequestDTO request) {
        // Buscar usuario por correo
        UsuariosEntity usuario = usuariosRepository.findByCorreo(request.getCorreo())
                .orElseThrow(() -> new RuntimeException("Credenciales inválidas"));

        // Verificar contraseña
        if (!passwordEncoder.matches(request.getContrasena(), usuario.getContrasena())) {
            throw new RuntimeException("Credenciales inválidas");
        }

        return entityToDto(usuario);
    }

    /*
     * Obtiene un usuario por su correo electrónico
     */
    @Transactional(readOnly = true)
    public UsuariosDTO obtenerUsuarioPorCorreo(String correo) {
        UsuariosEntity usuario = usuariosRepository.findByCorreo(correo)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con correo: " + correo));
        return entityToDto(usuario);
    }

    /*
     * Procesa el login de Google OAuth2
     */
    @Transactional
    public UsuariosDTO procesarLoginGoogle(String email, String nombre, String apellido) {
        // Buscar si el usuario ya existe
        return usuariosRepository.findByCorreo(email)
                .map(this::entityToDto)
                .orElseGet(() -> {
                    // Si no existe, crear nuevo usuario
                    RolesEntity rolUsuario = rolesRepository.findByNombreRol("USUARIO")
                            .orElseThrow(() -> new RuntimeException("Rol USUARIO no encontrado"));

                    Set<RolesEntity> roles = new HashSet<>();
                    roles.add(rolUsuario);

                    // Dividir el nombre si viene completo
                    String[] nombres = nombre != null ? nombre.split(" ", 2) : new String[]{"", ""};
                    String primerNombre = nombres.length > 0 ? nombres[0] : "";
                    String segundoNombre = nombres.length > 1 ? nombres[1] : "";

                    // Dividir el apellido si viene completo
                    String[] apellidos = apellido != null ? apellido.split(" ", 2) : new String[]{"", ""};
                    String primerApellido = apellidos.length > 0 ? apellidos[0] : "";
                    String segundoApellido = apellidos.length > 1 ? apellidos[1] : "";

                    UsuariosEntity nuevoUsuario = UsuariosEntity.builder()
                            .roles(roles)
                            .primerNombre(primerNombre)
                            .segundoNombre(segundoNombre)
                            .primerApellido(primerApellido)
                            .segundoApellido(segundoApellido)
                            .correo(email)
                            .contrasena(passwordEncoder.encode("GOOGLE_OAUTH2_" + System.currentTimeMillis()))
                            .fechaRegistro(LocalDateTime.now())
                            .build();

                    UsuariosEntity guardado = usuariosRepository.save(nuevoUsuario);
                    return entityToDto(guardado);
                });
    }

    /*
     * Genera un token JWT para el usuario
     */
    public String generarToken(UsuariosDTO usuario) {
        String nombreCompleto = usuario.getPrimerNombre() + " " +
                (usuario.getSegundoNombre() != null ? usuario.getSegundoNombre() + " " : "") +
                usuario.getPrimerApellido() +
                (usuario.getSegundoApellido() != null ? " " + usuario.getSegundoApellido() : "");

        return jwtService.generarToken(usuario.getCorreo(), usuario.getIdUsuario(), nombreCompleto);
    }

    /*
     * Convierte Entity a DTO
     */
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