package example.com.auth.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import example.com.auth.global.dto.GlobalErrorResponse;
import example.com.auth.global.exception.InvalidTokenException;

import example.com.auth.redis.RedisBlackListService;
import example.com.auth.user.domain.User;
import example.com.auth.user.repository.UserRepository;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;
    private final RedisBlackListService redisBlackListService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        if (path.equals("/logout") || path.equals("/signup") || path.equals("/login")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String token = resolveToken(request);

            // 토큰 존재 여부 확인
            if (token != null) {
                // 로그아웃된 토큰인지 확인
                if (redisBlackListService.isBlacklisted(token)) {
                    throw new JwtException("로그아웃된 토큰입니다.");
                }

                // 유효한 토큰이면 인증 처리
                if (jwtProvider.validateToken(token)) {
                    String username = jwtProvider.getUsername(token);

                    User user = userRepository.findByUsername(username)
                            .orElseThrow(() -> new RuntimeException("토큰의 사용자 정보를 찾을 수 없습니다."));

                    UserDetailsImp userDetails = new UserDetailsImp(user);
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    System.out.println("인증 완료: " + userDetails.getUsername());
                }
            }

            filterChain.doFilter(request, response);

        } catch (ExpiredJwtException e) {
            setErrorResponse(response, "EXPIRED_JWT", "토큰이 만료되었습니다.");
        } catch (JwtException | IllegalArgumentException e) {
            setErrorResponse(response, "INVALID_JWT", "유효하지 않은 JWT입니다.");
        } catch (Exception e) {
            setErrorResponse(response, "UNAUTHORIZED", "로그인이 필요합니다.");
        }
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // "Bearer " 제거
        }
        return null;
    }

    private void setErrorResponse(HttpServletResponse response, String code, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        GlobalErrorResponse error = GlobalErrorResponse.of(code, message);
        String json = new ObjectMapper().writeValueAsString(error);
        response.getWriter().write(json);
    }
}