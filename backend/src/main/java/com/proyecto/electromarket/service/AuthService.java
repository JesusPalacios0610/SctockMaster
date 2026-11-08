package com.proyecto.electromarket.service;

import com.proyecto.electromarket.dto.LoginRequest;
import com.proyecto.electromarket.dto.UsuarioAuthResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {

    // Validar credenciales, iniciar sesión de Spring Security y responder datos de usuario
    UsuarioAuthResponse login(LoginRequest request, HttpServletRequest httpRequest, HttpServletResponse httpResponse);

    // Obtener información del usuario con la sesión actual
    UsuarioAuthResponse obtenerUsuarioActual();

    // Cerrar la sesión
    void logout(HttpServletRequest httpRequest);
}
