package com.flawlesscoders.ambigu.utils.security;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtTokenProvider {
    private static final String SECRET="o+BtBTvTO5ihCqB9hIXb5bl0npGGFtxcKeiV4u4P0aAZwPUdC5Xo1qmlsGpF4qZYS+LxVr15AAInngBwS/eRg==";
    private static final long EXPIRATION_TIME = 86400000; // 1 day

    //Convert the 'SECRET' to a Secure Key
    private static final Key SECRET_KEY = Keys.hmacShaKeyFor(SECRET.getBytes());

    //Creamos los CLAIMS 
    public Map<String, Object> generateClaims(String username, String role, Boolean isLeader) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);

        if("WAITER".equals(role)){
            claims.put("isLeader", isLeader);
        }
        return claims;
    }

    //Generate a secure JWT token
    public String generateToken(String username, String role, Boolean isLeader) {
        return Jwts.builder()
                .setClaims(generateClaims(username, role, isLeader))
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SECRET_KEY, SignatureAlgorithm.HS512)
                .compact();
    }

    public boolean validateToken(String token){
        try{
            Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (JwtException e){
            System.out.println("Invalid JWT token" + e.getMessage());
            return false;
        }
    }

    public Claims getClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Date getExpirationDateFromToken(String token) {
        return getClaimsFromToken(token).getExpiration();
    }
}
