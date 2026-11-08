package com.proyecto.electromarket.config;

import com.proyecto.electromarket.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import tools.jackson.databind.ObjectMapper;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityContextRepository securityContextRepository() {
        return new HttpSessionSecurityContextRepository();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 1. Deshabilitar CSRF (API REST con frontend desacoplado)
            .csrf(AbstractHttpConfigurer::disable)

            // 2. Deshabilitar formularios HTML y pop-ups basic auth por defecto
            .formLogin(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable)

            // 3. Habilitar CORS reutilizando CorsConfig
            .cors(Customizer.withDefaults())

            // 4. Configurar SecurityContext explícito
            .securityContext(sc -> sc.securityContextRepository(securityContextRepository()))

            // 5. Configuración de permisos de rutas
            .authorizeHttpRequests(auth -> auth
                // Preflight OPTIONS permitido para CORS
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                // Ruta pública para iniciar sesión
                .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()

                // Permitir ruta de errores de Spring Boot para no enmascarar excepciones como 401
                .requestMatchers("/error").permitAll()

                // Rutas que Almacén y Administrador pueden ejecutar
                .requestMatchers(HttpMethod.GET, "/api/**").hasAnyRole("ADMIN", "ALMACEN")
                .requestMatchers(HttpMethod.POST, "/api/entradas").hasAnyRole("ADMIN", "ALMACEN")
                .requestMatchers(HttpMethod.POST, "/api/salidas").hasAnyRole("ADMIN", "ALMACEN")
                .requestMatchers(HttpMethod.POST, "/api/kardex/generar").hasAnyRole("ADMIN", "ALMACEN")

                // Auth logout y me: requieren estar autenticado (cualquier rol)
                .requestMatchers("/api/auth/logout", "/api/auth/me").authenticated()

                // Todo lo demás (POST, PUT, PATCH en productos, proveedores, etc.) solo ADMIN
                .requestMatchers("/api/productos/**").hasRole("ADMIN")
                .requestMatchers("/api/proveedores/**").hasRole("ADMIN")
                .anyRequest().hasRole("ADMIN")
            )

            // 6. Manejo de excepciones en JSON (sin HTML ni redirecciones)
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint(authenticationEntryPoint())
                .accessDeniedHandler(accessDeniedHandler())
            );

        return http.build();
    }

    // 401 Not Authenticated (sin sesión en ruta protegida)
    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, authException) -> {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");
            ErrorResponse error = new ErrorResponse("No autenticado. Debe iniciar sesión.");
            response.getWriter().write(objectMapper.writeValueAsString(error));
        };
    }

    // 403 Forbidden (sin permiso para la acción)
    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return (request, response, accessDeniedException) -> {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");
            ErrorResponse error = new ErrorResponse("Acceso denegado. No tiene permisos suficientes.");
            response.getWriter().write(objectMapper.writeValueAsString(error));
        };
    }
}
