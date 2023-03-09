package com.example.jwt.security.infrastructure;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.jwt.MessageResponse;
import com.example.jwt.refreshtoken.application.RefreshTokenService;
import com.example.jwt.refreshtoken.domain.RefreshToken;
import com.example.jwt.rol.domain.ERol;
import com.example.jwt.rol.domain.Rol;
import com.example.jwt.rol.infrastructure.RolRepo;
import com.example.jwt.security.application.JwtUtils;
import com.example.jwt.security.application.UserDetailsImpl;
import com.example.jwt.usuario.domain.Usuario;
import com.example.jwt.usuario.infrastructure.UserInfoResponse;
import com.example.jwt.usuario.infrastructure.UsuarioRepo;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

//@CrossOrigin(origins = "*", maxAge = 3600)
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600, allowCredentials = "true")
@RestController
@RequestMapping("/api/auth")
public class AuthController
{
    @Autowired
    AuthenticationManager authenticationManager;
    
    @Autowired
    UsuarioRepo usuarioRepo;

    @Autowired
    RolRepo rolRepo;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    RefreshTokenService refreshTokenService;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest)
    {
        Authentication authentication = authenticationManager
            .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);
        List<String> roles = userDetails.getAuthorities().stream()
            .map(item -> item.getAuthority())
            .toList();

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());
        ResponseCookie jwtRefreshCookie = jwtUtils.generateRefreshJwtCookie(refreshToken.getToken());
        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
            .header(HttpHeaders.SET_COOKIE, jwtRefreshCookie.toString())
            .body(new UserInfoResponse(
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest)
    {
        if (usuarioRepo.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
        }
        if (usuarioRepo.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
        }
        Usuario usuario = new Usuario(
            signUpRequest.getUsername(),
            signUpRequest.getEmail(),
            encoder.encode(signUpRequest.getPassword()));
        Set<String> strRoles = signUpRequest.getRoles();
        Set<Rol> roles = new HashSet<>();

        if (strRoles == null) {
            Rol userRole = rolRepo.findByName(ERol.ROLE_USER)
                .orElseThrow(()->new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            strRoles.forEach(rol -> {
                switch(rol) {
                    case "admin":
                        Rol adminRole = rolRepo.findByName(ERol.ROLE_ADMIN)
                            .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(adminRole);
                        break;
                    case "mod":
                        Rol modRole = rolRepo.findByName(ERol.ROLE_MODERATOR)
                            .orElseThrow(()->new RuntimeException("Error: Role is not found."));
                        roles.add(modRole);
                        break;
                    default:
                        Rol userRole = rolRepo.findByName(ERol.ROLE_USER)
                            .orElseThrow(()->new RuntimeException("Error: Role is not found."));
                        roles.add(userRole);
                }
            });
        }
        usuario.setRoles(roles);
        usuarioRepo.save(usuario);
        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }

    @PostMapping("/signout")
    public ResponseEntity<?> logoutUser()
    {
        Object principle = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principle.toString() != "anonymousUser")
        {
            Long userId = ((UserDetailsImpl) principle).getId();
            refreshTokenService.deleteByUserId(userId);
        }
        ResponseCookie cookie = jwtUtils.getCleanJwtCookie();
        ResponseCookie jwtRefreshCookie = jwtUtils.getCleanJwtRefreshCookie();

        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, cookie.toString())
            .header(HttpHeaders.SET_COOKIE, jwtRefreshCookie.toString())
            .body(new MessageResponse("You've been signed out"));
    }

    @PostMapping("/refreshtoken")
    public ResponseEntity<?> refreshToken(HttpServletRequest request)
    {
        String refreshToken = jwtUtils.getJwtRefreshFromCookies(request);
        if ((refreshToken != null) && (refreshToken.length() > 0))
        {
            return refreshTokenService.findByToken(refreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUsuario)
                .map(usuario -> {
                    // Desechamos el anterior y creamos uno nuevo.
                    refreshTokenService.deleteByUserId(usuario.getId());
                    RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(usuario.getId());
                    ResponseCookie jwtRefreshCookie = jwtUtils.generateRefreshJwtCookie(newRefreshToken.getToken());
                    ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(usuario);
                    return ResponseEntity.ok()
                        .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                        .header(HttpHeaders.SET_COOKIE, jwtRefreshCookie.toString())
                        .body(new MessageResponse("Token is refreshed successfully!"));
                })
                .orElseThrow(() -> new TokenRefreshException(refreshToken, "Refresh token is not in database!"));
        } 
        return ResponseEntity.badRequest().body(new MessageResponse("Refresh token is empty!"));
    }
}
