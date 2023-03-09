package com.example.jwt.rol.infrastructure;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.jwt.rol.domain.ERol;
import com.example.jwt.rol.domain.Rol;

@Repository
public interface RolRepo extends JpaRepository<Rol, Long>
{
    Optional<Rol> findByName(ERol nombre);    
}
