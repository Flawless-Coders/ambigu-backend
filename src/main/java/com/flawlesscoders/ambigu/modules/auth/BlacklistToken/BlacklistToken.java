package com.flawlesscoders.ambigu.modules.auth.BlacklistToken;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "blacklistTokens")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BlacklistToken {
    @Id
    private String token;
    private Date expirationDate;
}
