package example.com.auth.user.dto;


import example.com.auth.user.domain.Role;
import example.com.auth.user.domain.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "회원 응답 DTO")
public class UserResponseDto {

    @Schema(description = "유저 ID", example = "1")
    private Long id;

    @Schema(description = "유저명", example = "testuser")
    private String username;

    @Schema(description = "닉네임", example = "테스트유저")
    private String nickname;

    @Schema(description = "유저 권한", example = "USER")
    private Role role;

    @Builder
    public UserResponseDto(Long id, String username, String nickname, Role role) {
        this.id = id;
        this.username = username;
        this.nickname = nickname;
        this.role = role;
    }



    public static UserResponseDto from(User user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .role(user.getRole())
                .build();
    }
}