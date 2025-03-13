package com.flawlesscoders.ambigu.modules.auth.passwordReset;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Builder;
import lombok.Data;

@Document(collection = "password_reset_tokens")
@Data
@Builder
public class PasswordResetToken {
    @Id
    private String id;
    private String token;
    private String userEmail;
    private LocalDateTime expirationDate;
}
