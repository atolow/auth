package example.com.auth.security;

import example.com.auth.global.exception.InvalidTokenException;
import example.com.auth.user.domain.User;
import example.com.auth.user.repository.UserRepository;
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

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        try {
            String token = resolveToken(request);

            if (token != null && jwtProvider.validateToken(token)) {
                String username = jwtProvider.getUsername(token);

                User user = userRepository.findByUsername(username)
                        .orElseThrow(() -> new InvalidTokenException("토큰의 사용자 정보를 찾을 수 없습니다."));

                // ✅ UserDetails 객체 생성
                UserDetailsImp userDetails = new UserDetailsImp(user);

                // ✅ 인증 객체 생성 및 등록
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                SecurityContextHolder.getContext().setAuthentication(authentication);

                System.out.println("✅ 인증 완료: " + userDetails.getUsername());
            }

        } catch (Exception e) {
            // ✅ 유효하지 않은 토큰 처리
            throw new InvalidTokenException("유효하지 않은 인증 토큰입니다.");
        }

        filterChain.doFilter(request, response);
    }

    // ✅ Authorization 헤더에서 Bearer 토큰 추출
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // "Bearer " 제거 후 토큰 반환
        }
        return null;
    }
}