package com.flawlesscoders.ambigu.modules.auth.token;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface TokenRepository extends MongoRepository<Token, String> {
    boolean existsByTokenAndRevokedTrue(String token);
    List<Token> findByRevokedFalse();
}
