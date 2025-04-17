package example.com.auth.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "회원가입 요청 DTO")
public class SignupRequestDto {

    @Schema(description = "사용자 아이디 (영문, 숫자 포함 4~15자)", example = "testuser")
    @NotBlank(message = "username은 필수입니다.")
    private String username;

    @Schema(description = "비밀번호 (8자 이상)", example = "password123")
    @NotBlank(message = "password는 필수입니다.")
    private String password;

    @Schema(description = "닉네임", example = "Tester")
    @NotBlank(message = "nickname은 필수입니다.")
    private String nickname;

    public SignupRequestDto(String username, String password, String nickname) {
        this.username = username;
        this.password = password;
        this.nickname = nickname;
    }
}
