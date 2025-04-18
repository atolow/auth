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
     * 기본 만료 시간으로 토큰 발급
     */
    public String generateToken(String username, Role role) {
        return generateToken(username, role, EXPIRATION_TIME);
    }

    /**
     * 사용자 지정 만료 시간으로 토큰 발급
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


    public long getRemainingExpiration(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        Date expiration = claims.getExpiration();
        long now = System.currentTimeMillis();
        return expiration.getTime() - now;
    }

    /**
     * 토큰 유효성 검사 (만료 여부 포함)
     */
    public boolean validateToken(String token) {
        try {
            Claims claims = parseClaims(token);
            return !claims.getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            throw e; // 필터에서 따로 처리
        } catch (JwtException | IllegalArgumentException e) {
            throw new InvalidTokenException("유효하지 않은 JWT입니다.");
        }
    }

    /**
     * 토큰에서 username 추출
     */
    public String getUsername(String token) {
        return parseClaims(token).getSubject();
    }

    /**
     * 토큰에서 Role 추출
     */
    public Role getRole(String token) {
        try {
            String roleString = parseClaims(token).get("role", String.class);
            return Role.valueOf(roleString);
        } catch (Exception e) {
            throw new InvalidTokenException("JWT에서 role을 추출할 수 없습니다.");
        }
    }

    /**
     * Claims 파싱 (공통 처리용)
     */
    private Claims parseClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims(); // 만료됐어도 claims 추출 가능
        } catch (JwtException | IllegalArgumentException e) {
            throw new InvalidTokenException("JWT 파싱 실패");
        }
    }
    public Key getSecretKey() {
        return this.secretKey;
    }
}