package com.example.jwt.security.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.jwt.usuario.domain.Usuario;
import com.example.jwt.usuario.infrastructure.UsuarioRepo;

import jakarta.transaction.Transactional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    UsuarioRepo usuarioRepo;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepo.findByUsername(username)
            .orElseThrow(()->new UsernameNotFoundException("Usuario no encontrado con username: " + username));
        return UserDetailsImpl.build(usuario);
    }
    
}
