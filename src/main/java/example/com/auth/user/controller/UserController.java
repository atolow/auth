package example.com.auth.user.controller;


import example.com.auth.global.dto.GlobalErrorResponse;
import example.com.auth.redis.RedisBlackListService;
import example.com.auth.security.JwtProvider;
import example.com.auth.security.UserDetailsImp;
import example.com.auth.user.domain.User;
import example.com.auth.user.dto.*;
import example.com.auth.user.service.UserService;
import example.com.auth.utils.UserUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "User API", description = "회원가입, 로그인, 권한 부여 로그아웃 유저 관련 API")
public class UserController {

    private final UserService userService;
    private final JwtProvider jwtProvider;
    private final RedisBlackListService redisBlackListService;

    @Operation(summary = "회원가입", description = "username, password, nickname을 받아 회원가입합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원가입 성공",
                    content = @Content(schema = @Schema(implementation = UserResponseDto.class))),
            @ApiResponse(responseCode = "409", description = "이미 가입된 사용자",
                    content = @Content(schema = @Schema(implementation = GlobalErrorResponse.class)))
    })
    @PostMapping("/signup")
    public ResponseEntity<UserResponseDto> creatUser(@RequestBody SignupRequestDto request) {
        return ResponseEntity.ok(userService.signup(request));
    }

    @Operation(summary = "로그인", description = "username과 password를 받아 토큰을 발급합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공",
                    content = @Content(schema = @Schema(implementation = LoginResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "잘못된 자격 증명",
                    content = @Content(schema = @Schema(implementation = GlobalErrorResponse.class)))
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto request) {
        return ResponseEntity.ok(userService.login(request));
    }

    @Operation(summary = "관리자 권한 부여", description = "ADMIN 권한의 사용자가 target 유저에게 관리자 권한을 부여합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "권한 부여 성공",
                    content = @Content(schema = @Schema(implementation = UserResponseDto.class))),
            @ApiResponse(responseCode = "403", description = "권한 부족",
                    content = @Content(schema = @Schema(implementation = GlobalErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "유저를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = GlobalErrorResponse.class)))
    })
    @PatchMapping("/users/{userId}/roles")
    public ResponseEntity<UserResponseDto> grantAdmin(@PathVariable Long userId,
                                                      @AuthenticationPrincipal UserDetailsImp userDetails) {
        User user = UserUtils.getUser(userDetails);
        UserResponseDto updatedUser = userService.grantAdminRole(userId, user);
        return ResponseEntity.ok(updatedUser);
    }


    @Operation(
            summary = "JWT 로그아웃",
            description = "Authorization 헤더에 포함된 JWT 토큰을 블랙리스트에 등록하여 로그아웃 처리합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그아웃 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 Authorization 헤더 형식")
    })
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String bearerToken) {
        if (!bearerToken.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body("잘못된 Authorization 헤더 형식입니다.");
        }

        String token = bearerToken.substring(7);
        long expiration = jwtProvider.getRemainingExpiration(token); // 남은 만료 시간(ms)

        redisBlackListService.blacklistToken(token, expiration);
        return ResponseEntity.ok().body("로그아웃 성공");
    }
}
