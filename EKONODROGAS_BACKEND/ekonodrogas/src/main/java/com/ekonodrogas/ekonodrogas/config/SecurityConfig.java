package com.ekonodrogas.ekonodrogas.config;

import com.ekonodrogas.ekonodrogas.service.UserDetailServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.WebExpressionAuthorizationManager;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.web.filter.HiddenHttpMethodFilter;


@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws  Exception {

        // http://localhost:8080/swagger-ui/index.html

        http.authorizeHttpRequests( auth -> {
            auth.requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll();
            auth.requestMatchers("/login", "/home").permitAll();
            auth.requestMatchers("/clases").hasAnyRole("ADMIN", "USER");
            auth.requestMatchers("/clases/crear").hasRole("ADMIN");
            auth.requestMatchers("/clases/editar").hasRole("ADMIN");
            auth.requestMatchers("/clases/editar/{id}").hasRole("ADMIN");

            auth.anyRequest().authenticated();
        })
                .formLogin( formLogin -> {
            formLogin.loginPage("/login");
            formLogin.loginProcessingUrl("/login");
            formLogin.usernameParameter("username");
            formLogin.passwordParameter("password");
            formLogin.failureUrl("/login?error");
            formLogin.successHandler(successHandler());
            formLogin.permitAll();
        })
                .logout(logout -> {
            logout.logoutUrl("/logout");
            logout.logoutSuccessUrl("/loginPage?logout");
            logout.invalidateHttpSession(true);
            logout.deleteCookies("JSESSIONID"); // borra cookies
        }).csrf(AbstractHttpConfigurer::disable);


        return http.build();
    }


    @Bean // Define que se hace al iniciar sesión
    public AuthenticationSuccessHandler successHandler() {
        return (request, response, auth) -> {
            response.sendRedirect("/home");
        };
    }


    @Bean //Administra las autenticaciones en general
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(UserDetailServiceImpl userDetailService) throws Exception {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder()); // Validar password cifradas
        provider.setUserDetailsService(userDetailService); // Carga usuarios desde DB
        return provider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Cifra password con algoritmo BCrypt
    }

    @Bean
    public HiddenHttpMethodFilter hiddenHttpMethodFilter() {
        return new HiddenHttpMethodFilter(); // Permite uso métodos PU o Delete en form HTML
    }

}





