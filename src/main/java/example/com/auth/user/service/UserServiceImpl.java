package example.com.auth.user.service;

import example.com.auth.global.exception.CustomAccessDeniedException;
import example.com.auth.global.exception.InvalidCredentialsException;
import example.com.auth.global.exception.UserAlreadyExistsException;
import example.com.auth.global.exception.CustomUserNotFoundException;
import example.com.auth.security.JwtProvider;
import example.com.auth.user.domain.Role;
import example.com.auth.user.domain.User;
import example.com.auth.user.dto.LoginRequestDto;
import example.com.auth.user.dto.LoginResponseDto;
import example.com.auth.user.dto.SignupRequestDto;
import example.com.auth.user.dto.UserResponseDto;
import example.com.auth.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor

public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    @Override
    public UserResponseDto signup(SignupRequestDto request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException("이미 가입된 사용자입니다.");
        }

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .nickname(request.getNickname())
                .role(Role.USER)
                .build();

        userRepository.save(user);

        return UserResponseDto.from(user);
    }

    public LoginResponseDto login(LoginRequestDto request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("아이디 또는 비밀번호가 올바르지 않습니다."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("아이디 또는 비밀번호가 올바르지 않습니다.");
        }

        String token = jwtProvider.generateToken(user.getUsername(), user.getRole());
        return new LoginResponseDto(token);
    }

    public UserResponseDto grantAdminRole(Long userId, User authentication) {
        // 요청한 사용자 확인
        User requester = userRepository.findByUsername(authentication.getUsername())
                .orElseThrow(() -> new CustomUserNotFoundException("요청자 계정을 찾을 수 없습니다."));
        if (!requester.getRole().equals(Role.ADMIN)) {
            throw new CustomAccessDeniedException("관리자 권한이 필요한 요청입니다. 접근 권한이 없습니다.");
        }
        // 대상 유저 찾기
        User target = userRepository.findById(userId)
                .orElseThrow(() -> new CustomUserNotFoundException("대상 사용자를 찾을 수 없습니다."));

        // 관리자 권한 없으면 예외

        userRepository.save(target);

        return UserResponseDto.from(target);
    }
}
