package com.proyecto.electromarket.repository;

import com.proyecto.electromarket.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    // Buscar un usuario por su nombre de usuario (login)
    Optional<Usuario> findByUsuario(String usuario);

    // Comprobar si ya existe un usuario con ese nombre
    boolean existsByUsuario(String usuario);
}
