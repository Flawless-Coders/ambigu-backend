package com.flawlesscoders.ambigu.modules.auth.passwordReset;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class PasswordResetController {
    private final PasswordResetService passwordResetService;

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> request) {
        passwordResetService.sendPasswordResetEmail(request.get("email"));
        return ResponseEntity.ok("Si el usuario existe, se enviará un correo con las instrucciones para restablecer la contraseña");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request) {
        passwordResetService.resetPassword(request.get("token"), request.get("newPassword"));
        return ResponseEntity.ok("Contraseña actualizada exitosamente");
    }

    @PostMapping("/validate-reset")
    public ResponseEntity<?> validateToken(@RequestBody Map<String, String> request) {
        return passwordResetService.validateToken(request.get("token")) ?
            ResponseEntity.ok("Token valido") :
            ResponseEntity.badRequest().build();
    }
}

