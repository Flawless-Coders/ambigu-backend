package com.flawlesscoders.ambigu.modules.auth.blacklistToken;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BlacklistTokenRepository extends MongoRepository<BlacklistToken, String> {
    boolean existsByToken(String token);
}
