package example.com.auth.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "로그인 요청 DTO")
public class LoginRequestDto {

    @Schema(description = "유저 아이디", example = "testuser")
    private String username;

    @Schema(description = "비밀번호", example = "password1234")
    private String password;


    public LoginRequestDto(String username, String password) {
        this.username = username;
        this.password = password;
    }
}