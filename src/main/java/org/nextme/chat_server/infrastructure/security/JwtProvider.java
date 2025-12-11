package org.nextme.chat_server.infrastructure.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtProvider {

    private final JwtProperties jwt;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwt.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    //Jwt 토큰에서 userId 가져오기
    public UUID getUserId(String token) {
        try{
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String id = claims.get("sub", String.class);

            return UUID.fromString(id);

        } catch (Exception e) {
            throw new RuntimeException("유효하지 않은 JWT 토큰 입니다.",e);
        }
    }

    //Jwt 토큰에서 userName 가져오기
    public String getUserName(String token) {
        try{
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            return claims.get("name", String.class);

        } catch (Exception e) {
            throw new RuntimeException("유효하지 않은 JWT 토큰 입니다.",e);
        }
    }
}
