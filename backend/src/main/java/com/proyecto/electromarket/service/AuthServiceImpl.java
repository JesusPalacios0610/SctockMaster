package com.proyecto.electromarket.service;

import com.proyecto.electromarket.dto.LoginRequest;
import com.proyecto.electromarket.dto.UsuarioAuthResponse;
import com.proyecto.electromarket.entity.Usuario;
import com.proyecto.electromarket.repository.UsuarioRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.text.Normalizer;
import java.util.List;

@Service
public class AuthServiceImpl implements AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final SecurityContextRepository securityContextRepository;

    public AuthServiceImpl(UsuarioRepository usuarioRepository,
                           PasswordEncoder passwordEncoder,
                           SecurityContextRepository securityContextRepository) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.securityContextRepository = securityContextRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioAuthResponse login(LoginRequest request, HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        String usernameLimpio = request.getUsuario() != null ? request.getUsuario().trim() : "";

        // 1. Buscar usuario (CHAR(50) con trim)
        Usuario usuario = usuarioRepository.findByUsuario(usernameLimpio)
                .orElseThrow(() -> new BadCredentialsException("Correo o contraseña incorrectos."));

        // 2. Comparar contraseñas con BCrypt
        if (!passwordEncoder.matches(request.getContrasena(), usuario.getContrasena())) {
            throw new BadCredentialsException("Correo o contraseña incorrectos.");
        }

        // 3. Normalizar el rol para Spring Security (ROLE_ADMIN, ROLE_ALMACEN)
        String authority = normalizarRol(usuario.getRol());
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(authority));

        // 4. Crear la autenticación con el login del usuario como principal
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                usuario.getUsuario(),
                null,
                authorities
        );

        // 5. Cambiar ID de sesión solo si ya existía una sesión previa (evita IllegalStateException)
        if (httpRequest.getSession(false) != null) {
            httpRequest.changeSessionId();
        }

        // 6. Crear y guardar el SecurityContext explícitamente en la sesión (crea la sesión si no existe)
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        securityContextRepository.saveContext(context, httpRequest, httpResponse);

        return new UsuarioAuthResponse(
                usuario.getIdUsuario(),
                usuario.getNombre(),
                usuario.getRol()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioAuthResponse obtenerUsuarioActual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            throw new BadCredentialsException("No autenticado. Debe iniciar sesión.");
        }

        String username = auth.getName();
        Usuario usuario = usuarioRepository.findByUsuario(username.trim())
                .orElseThrow(() -> new BadCredentialsException("No autenticado. Debe iniciar sesión."));

        return new UsuarioAuthResponse(
                usuario.getIdUsuario(),
                usuario.getNombre(),
                usuario.getRol()
        );
    }

    @Override
    public void logout(HttpServletRequest httpRequest) {
        HttpSession session = httpRequest.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        SecurityContextHolder.clearContext();
    }

    /**
     * Normaliza "Administrador" -> "ROLE_ADMIN", "Almacén" / "Almacen" -> "ROLE_ALMACEN"
     * Remueve acentos y pasa a mayúsculas.
     */
    private String normalizarRol(String rol) {
        if (rol == null) {
            return "ROLE_ALMACEN";
        }
        String sinAcentos = Normalizer.normalize(rol, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .trim()
                .toUpperCase();

        if (sinAcentos.contains("ADMIN")) {
            return "ROLE_ADMIN";
        }
        return "ROLE_ALMACEN";
    }
}
