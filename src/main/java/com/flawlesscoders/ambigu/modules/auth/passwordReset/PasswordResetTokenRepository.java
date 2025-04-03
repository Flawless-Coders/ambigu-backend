package com.flawlesscoders.ambigu.modules.auth.passwordReset;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface PasswordResetTokenRepository extends MongoRepository<PasswordResetToken, String> {
    Optional<PasswordResetToken> findByToken(String token);
    void deleteByUserEmail(String userEmail);
}