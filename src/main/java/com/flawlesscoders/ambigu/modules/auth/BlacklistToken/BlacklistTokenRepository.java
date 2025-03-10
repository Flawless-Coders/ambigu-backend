package com.flawlesscoders.ambigu.modules.auth.BlacklistToken;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BlacklistTokenRepository extends MongoRepository<BlacklistToken, String> {
    boolean existsByToken(String token);
}
