package example.com.auth.global.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GlobalErrorResponse {
    private ErrorDetail error;

    public GlobalErrorResponse(ErrorDetail error) {
        this.error = error;
    }

    @Getter
    @NoArgsConstructor
    public static class ErrorDetail {
        private String code;
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