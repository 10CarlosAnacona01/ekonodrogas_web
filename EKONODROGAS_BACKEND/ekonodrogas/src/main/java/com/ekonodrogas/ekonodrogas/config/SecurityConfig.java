package com.ekonodrogas.ekonodrogas.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilterConfig jwtAuthFilter;
    private final UserDetailsService userDetailsService;
    private final OAuth2AuthenticationSuccessHandlerConfig oauth2SuccessHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                // Deshabilitar CSRF (no necesario para APIs REST con JWT)
                .csrf(AbstractHttpConfigurer::disable)

                // CONFIGURACIÓN CORS INTEGRADA (MUY IMPORTANTE)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // Sesiones sin estado (stateless)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Configuración de autorización
                .authorizeHttpRequests(auth -> {
                    // Swagger públicos
                    auth.requestMatchers("/v3/api-docs/**",
                                         "/swagger-ui/**",
                                         "/swagger-ui.html"
                    ).permitAll();

                    // Endpoints de autenticación, OAuth2 y ver productos públicos
                    auth.requestMatchers(
                            "/api/productos/**",
                            "/api/auth/**",
                            "/api/categorias/**",
                            "/api/ofertas/**",
                            "/login/**",
                            "/oauth2/**"
                    ).permitAll();

                    // Endpoints que requieren USUARIO o ADMINISTRADOR
                    auth.requestMatchers("/api/carrito/**", "/api/pagos/**")
                            .hasAnyRole("ADMINISTRADOR", "USUARIO");

                    // Endpoints solo para ADMINISTRADOR
                    auth.requestMatchers(
                            "/api/usuarios/**",
                            "/api/ventas/**",
                            "/api/detalle-ventas/**",
                            "/api/roles/**"
                    ).hasRole("ADMINISTRADOR");

                    // Resto requiere autenticación
                    auth.anyRequest().authenticated();
                })

                // Configuración OAuth2
                .oauth2Login(oauth2 -> oauth2
                        .successHandler(oauth2SuccessHandler)
                )

                // Agregar filtro JWT ANTES del filtro de autenticación
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)

                // Configurar el provider de autenticación
                .authenticationProvider(authenticationProvider());

        return http.build();
    }

    /*
     * Configuración CORS integrada con Spring Security.
     * Debe estar dentro de SecurityConfig para funcionar con JWT
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Orígenes permitidos (frontend)
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:5501",
                "http://127.0.0.1:5501",
                "http://localhost:5500",
                "http://127.0.0.1:5500"
        ));

        // Métodos HTTP permitidos
        configuration.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"
        ));

        // Headers permitidos (IMPORTANTE: incluir Authorization para JWT)
        configuration.setAllowedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type",
                "Accept",
                "Origin",
                "X-Requested-With"
        ));

        // Headers expuestos (visible para el frontend)
        configuration.setExposedHeaders(Arrays.asList(
                "Authorization",
                "Content-Disposition"
        ));

        // Permitir credenciales (cookies, headers de autorización)
        configuration.setAllowCredentials(true);

        // Tiempo de caché para preflight (1 hora)
        configuration.setMaxAge(3600L);

        // Aplicar configuración a todos los endpoints /api/**
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);
        source.registerCorsConfiguration("/oauth2/**", configuration);
        source.registerCorsConfiguration("/login/**", configuration);

        return source;
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}



