package com.flawlesscoders.ambigu.modules.auth;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.flawlesscoders.ambigu.modules.user.base.Role;
import com.flawlesscoders.ambigu.modules.user.base.User;
import com.flawlesscoders.ambigu.modules.user.base.UserRepository;
import com.flawlesscoders.ambigu.modules.user.waiter.Waiter;
import com.flawlesscoders.ambigu.utils.security.JwtTokenProvider;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    public String login(AuthRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Comparar la contraseña ingresada con la almacenada (BCrypt)
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Usuario o contraseña incorrectos");
        }

        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        // Platform restriction
        if (user.getRole() == Role.ADMIN && !"WEB".equals(request.getPlatform())) {
            throw new IllegalArgumentException("Los administradores solo pueden iniciar sesión desde la plataforma web");
        }
        if (user.getRole() == Role.WAITER && !"MOBILE".equals(request.getPlatform())) {
            throw new IllegalArgumentException("Los meseros solo pueden iniciar sesión desde la plataforma móvil");
        }

        Boolean isLeader = null;
        if (user instanceof Waiter) {
            Waiter waiter = (Waiter) user;
            isLeader = waiter.isLeader();
        }

        return jwtTokenProvider.generateToken(user.getEmail(), user.getRole().name(), isLeader);
    }

    public ResponseEntity<String> validateToken( String token) {
        if(jwtTokenProvider.validateToken(token)){
            return ResponseEntity.ok("Token valido");
        } else {
            return ResponseEntity.status(401).body("Token invalido");
        }
    }


}