package com.flawlesscoders.ambigu.modules.auth.passwordReset;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.flawlesscoders.ambigu.modules.user.base.User;
import com.flawlesscoders.ambigu.modules.user.base.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Service
@RequiredArgsConstructor
public class PasswordResetService {
    private final PasswordResetTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final JavaMailSender mailSender;
    private final BCryptPasswordEncoder passwordEncoder;

    private static final int EXPIRATION_TIME = 15;

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
            
            String apiUrl;
            try {
                apiUrl = java.net.InetAddress.getLocalHost().getHostAddress();
                apiUrl = "http://" + apiUrl + ":8080"; // Assuming the application runs on port 8080
            } catch (java.net.UnknownHostException e) {
                apiUrl = "http://localhost:8080"; // Default to localhost if the IP address cannot be determined
            }
            String resetLink = apiUrl + "/reset-password?token=" + token;

            sendEmail(email, resetLink);
        }
    }

    public void sendEmail (String to, String resetLink){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Recuperación de contraseña");
        message.setText("Haz clic en el siguiente enlace para restablecer tu contraseña: " + resetLink);
        mailSender.send(message);
    }

    public boolean validateToken(String token) {
        Optional<PasswordResetToken> resetToken = tokenRepository.findByToken(token);
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
