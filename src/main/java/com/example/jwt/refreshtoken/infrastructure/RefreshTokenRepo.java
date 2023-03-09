package com.example.jwt.refreshtoken.infrastructure;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import com.example.jwt.refreshtoken.domain.RefreshToken;
import com.example.jwt.usuario.domain.Usuario;

@Repository
public interface RefreshTokenRepo extends JpaRepository<RefreshToken, Long>
{
    Optional<RefreshToken> findByToken(String token);

    @Modifying
    int deleteByUsuario(Usuario usuario);
}
