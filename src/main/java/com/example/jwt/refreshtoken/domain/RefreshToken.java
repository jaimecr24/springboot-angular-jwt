package com.example.jwt.refreshtoken.domain;

import java.time.Instant;

import com.example.jwt.usuario.domain.Usuario;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

@Entity(name = "refreshtoken")
@Getter
@Setter
public class RefreshToken
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Estaba con AUTO, pero entonces busca otra tabla _seq
    private long id;
    
    @OneToOne
    @JoinColumn(name = "usuario_id", referencedColumnName = "id")
    private Usuario usuario;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(nullable = false)
    private Instant expirvDate;
}
