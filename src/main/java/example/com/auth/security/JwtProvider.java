package example.com.auth.security;

import example.com.auth.global.exception.InvalidTokenException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import example.com.auth.user.domain.Role;
import java.security.Key;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtProvider {

    @Value("${jwt.secret-key}")
    private String secretKeyString;

    private Key secretKey;

    @Value("${jwt.secret-time}")
    private long EXPIRATION_TIME;

    @PostConstruct
    public void init() {
        this.secretKey = Keys.hmacShaKeyFor(secretKeyString.getBytes());
    }

    /**
     * 기본 만료 시간(1시간) 토큰 발급
     */
    public String generateToken(String username, Role role) {
        return generateToken(username, role, EXPIRATION_TIME);
    }

    /**
     * 사용자 정의 만료 시간 토큰 발급
     */
    public String generateToken(String username, Role role, long expireMillis) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expireMillis);

        return Jwts.builder()
                .setSubject(username)
                .claim("role", role.name())
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 토큰 유효성 검사
     */
    public boolean validateToken(String token) {
        try {
            Jws<Claims> claimsJws = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);

            return !claimsJws.getBody().getExpiration().before(new Date());

        } catch (ExpiredJwtException e) {
            // 만료된 토큰 → 필터에서 catch 가능
            throw e;
        } catch (JwtException | IllegalArgumentException e) {
            // 유효하지 않은 형식
            throw new InvalidTokenException("유효하지 않은 JWT입니다.");
        }
    }

    /**
     * 토큰에서 username 추출
     */
    public String getUsername(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (Exception e) {
            throw new InvalidTokenException("JWT에서 username을 추출할 수 없습니다.");
        }
    }

    /**
     * 토큰에서 role 추출 (선택적)
     */
    public Role getRole(String token) {
        try {
            String roleString = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .get("role", String.class);
            return Role.valueOf(roleString);
        } catch (Exception e) {
            throw new InvalidTokenException("JWT에서 role을 추출할 수 없습니다.");
        }
    }
}