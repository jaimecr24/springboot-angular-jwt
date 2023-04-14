package com.example.jwt.refreshtoken.application;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.jwt.refreshtoken.domain.RefreshToken;
import com.example.jwt.refreshtoken.infrastructure.RefreshTokenRepo;
import com.example.jwt.security.infrastructure.TokenRefreshException;
import com.example.jwt.usuario.infrastructure.UsuarioRepo;

import jakarta.transaction.Transactional;

@Service
public class RefreshTokenService
{   
    @Value("${jwtRefreshExpirationMs}") 
    private Long refreshTokenDurationMs;

    @Autowired
    private RefreshTokenRepo refreshTokenRepo;

    @Autowired
    private UsuarioRepo usuarioRepo;

    public Optional<RefreshToken> findByToken(String token)
    {
        return refreshTokenRepo.findByToken(token);
    }

    public RefreshToken createRefreshToken(Long userId)
    {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUsuario(usuarioRepo.findById(userId).get());
        refreshToken.setExpirvDate(Instant.now().plusMillis(refreshTokenDurationMs));
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken = refreshTokenRepo.save(refreshToken);
        return refreshToken;
    }

    public RefreshToken verifyExpiration(RefreshToken token)
    {
        if (token.getExpirvDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepo.delete(token);
            throw new TokenRefreshException(token.getToken(), "Refresh token was expired. Please make a new signin request");
        }
        return token;
    }

    @Transactional
    public int deleteByUserId(Long userId)
    {
        return refreshTokenRepo.deleteByUsuario(usuarioRepo.findById(userId).get());
    }
}
