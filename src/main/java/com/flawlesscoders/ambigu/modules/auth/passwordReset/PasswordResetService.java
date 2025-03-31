package com.flawlesscoders.ambigu.modules.auth.passwordReset;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import com.flawlesscoders.ambigu.modules.user.base.User;
import com.flawlesscoders.ambigu.modules.user.base.UserRepository;
import com.flawlesscoders.ambigu.utils.email.EmailService;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Service
@RequiredArgsConstructor
public class PasswordResetService {
    private final PasswordResetTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final BCryptPasswordEncoder passwordEncoder;

    private static final int EXPIRATION_TIME = 15;
    @Value("${frontend.url}")
    private String url;

    public void sendPasswordResetEmail(String email){
        Optional<User> user = userRepository.findByEmail(email);
        if(user.isPresent()){
            String token = UUID.randomUUID().toString();
            LocalDateTime expirationDate = LocalDateTime.now().plusMinutes(EXPIRATION_TIME);

            tokenRepository.deleteByUserEmail(email); //Elimina tokens previos
            tokenRepository.save(PasswordResetToken.builder()
                    .token(token)
                    .userEmail(email)
                    .expirationDate(expirationDate)
                    .build());

            String resetLink = url + "/password-recovery?token=" + token;

            Map<String, Object> templateModel = new HashMap<>();
            templateModel.put("name", user.get().getName());
            templateModel.put("resetLink", resetLink);
            templateModel.put("expirationTime", EXPIRATION_TIME);

            emailService.sendHtmlEmail(
                email, 
                "Recuperación de contraseña - Ambigú",
                "recover-password-email",
                templateModel
            );
        }
    }

    public boolean validateToken(String token) {
        Optional<PasswordResetToken> resetToken = tokenRepository.findByToken(token);
        System.out.println("Token: " + token);
        System.out.println("El token está en la bd: " + resetToken.isPresent());
        System.out.println("Fecha de vencimiento"+resetToken.get().getExpirationDate());
        System.out.println(LocalDateTime.now());
        return resetToken.isPresent() && resetToken.get().getExpirationDate().isAfter(LocalDateTime.now());
    }

    public void resetPassword(String token, String password) {
        Optional<PasswordResetToken> resetToken = tokenRepository.findByToken(token);
        if(resetToken.isPresent() && validateToken(token)){
            User user = userRepository.findByEmail(resetToken.get().getUserEmail()).get();
            password = passwordEncoder.encode(password);
            user.setPassword(password);
            userRepository.save(user);
            tokenRepository.deleteByUserEmail(user.getEmail());
        }else{
            throw new IllegalArgumentException("Token inválido o expirado");
        }   
    }


}
