package com.example.jwt.usuario.infrastructure;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserInfoResponse 
{
    private Long id;
    private String username;
    private String email;
    private List<String> roles;   
}
