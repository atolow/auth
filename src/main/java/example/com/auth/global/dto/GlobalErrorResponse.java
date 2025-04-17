package example.com.auth.global.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "공통 에러 응답 형식")
public class GlobalErrorResponse {

    @Schema(description = "에러 상세 정보")
    private ErrorDetail error;

    public GlobalErrorResponse(ErrorDetail error) {
        this.error = error;
    }

    @Getter
    @NoArgsConstructor
    @Schema(description = "에러 상세 정보")
    public static class ErrorDetail {

        @Schema(description = "에러 코드", example = "INVALID_CREDENTIALS")
        private String code;

        @Schema(description = "에러 메시지", example = "아이디 또는 비밀번호가 올바르지 않습니다.")
        private String message;

        public ErrorDetail(String code, String message) {
            this.code = code;
            this.message = message;
        }
    }

    // 팩토리 메서드
    public static GlobalErrorResponse of(String code, String message) {
        return new GlobalErrorResponse(new ErrorDetail(code, message));
    }
}