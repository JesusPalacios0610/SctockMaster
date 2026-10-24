package com.proyecto.electromarket.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // Habilita CORS para todas las rutas que comiencen con /api/
        registry.addMapping("/api/**")
                // Permite peticiones únicamente desde el puerto donde corre Vite/React
                .allowedOrigins("http://localhost:5173")
                // Métodos HTTP permitidos
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                // Cabeceras permitidas (incluyendo Content-Type, Authorization, etc.)
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
