package com.proyecto.electromarket.controller;

import com.proyecto.electromarket.dto.ErrorResponse;
import com.proyecto.electromarket.dto.LoginRequest;
import com.proyecto.electromarket.dto.UsuarioAuthResponse;
import com.proyecto.electromarket.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // POST /api/auth/login -> Inicia sesión por cookie y guarda SecurityContext
    @PostMapping("/login")
    public ResponseEntity<UsuarioAuthResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) {
        UsuarioAuthResponse response = authService.login(request, httpRequest, httpResponse);
        return ResponseEntity.ok(response);
    }

    // POST /api/auth/logout -> Invalida la sesión HTTP
    @PostMapping("/logout")
    public ResponseEntity<ErrorResponse> logout(HttpServletRequest httpRequest) {
        authService.logout(httpRequest);
        return ResponseEntity.ok(new ErrorResponse("Sesión cerrada exitosamente"));
    }

    // GET /api/auth/me -> Devuelve los datos del usuario con la sesión activa
    @GetMapping("/me")
    public ResponseEntity<UsuarioAuthResponse> me() {
        return ResponseEntity.ok(authService.obtenerUsuarioActual());
    }
}
