package com.example.jwt.usuario.infrastructure;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.jwt.usuario.domain.Usuario;

@Repository
public interface UsuarioRepo extends JpaRepository<Usuario, Long>
{
    Optional<Usuario> findByUsername(String username);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
}
