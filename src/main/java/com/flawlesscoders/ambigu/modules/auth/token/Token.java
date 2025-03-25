package com.flawlesscoders.ambigu.modules.auth.token;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "tokens")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Token {
    @Id
    private String token;
    private String username;
    private Date expiration;
    private boolean revoked;
    public boolean isActive(){
        return !revoked && (expiration == null || expiration.after(new Date()));
    }
}
