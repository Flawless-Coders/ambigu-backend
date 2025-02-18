package com.flawlesscoders.ambigu.modules.auth;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import com.flawlesscoders.ambigu.modules.user.base.Role;
import com.flawlesscoders.ambigu.modules.user.base.User;
import com.flawlesscoders.ambigu.modules.user.base.UserRepository;
import com.flawlesscoders.ambigu.utils.security.JwtTokenProvider;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    public String login(AuthRequest request) {
        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
        } catch (Exception e) {
            throw new BadCredentialsException("Usuario o contraseña incorrectos");
        }

        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Platform restriction
        if (user.getRole() == Role.ADMIN && !"WEB".equals(request.getPlatform())) {
            throw new IllegalArgumentException("Los administradores solo pueden iniciar sesión desde la plataforma web");
        }
        if (user.getRole() == Role.WAITER && !"MOBILE".equals(request.getPlatform())) {
            throw new IllegalArgumentException("Los meseros solo pueden iniciar sesión desde la plataforma móvil");
        }

        return jwtTokenProvider.generateToken(user.getEmail());
    }
}