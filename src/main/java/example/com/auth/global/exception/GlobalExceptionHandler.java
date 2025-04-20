package example.com.auth.global.exception;

import example.com.auth.global.dto.GlobalErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@ControllerAdvice
public class GlobalExceptionHandler {

    //로그인 실패 (중복유저가 있음)
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<GlobalErrorResponse> handleUserAlreadyExists(UserAlreadyExistsException ex) {
        GlobalErrorResponse error = GlobalErrorResponse.of("USER_ALREADY_EXISTS", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error); // 409
    }
    //로그인 실패(비밀번호 불일치)
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<GlobalErrorResponse> handleInvalidCredentials(InvalidCredentialsException ex) {
        GlobalErrorResponse error = GlobalErrorResponse.of("INVALID_CREDENTIALS", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error); // 401
    }
    @ExceptionHandler(CustomAccessDeniedException.class)
    public ResponseEntity<GlobalErrorResponse> handleAccessDeniedException(CustomAccessDeniedException ex) {
        GlobalErrorResponse error = GlobalErrorResponse.of("ACCESS_DENIED", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error); // 401
    }
    // 유저가 존재하지 않을 때
    @ExceptionHandler(CustomUserNotFoundException.class)
    public ResponseEntity<GlobalErrorResponse> handleUserNotFoundException(CustomUserNotFoundException ex) {
        GlobalErrorResponse error = GlobalErrorResponse.of("USER_NOT_FOUND", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error); // 401
    }
    //토근이 만료됨
    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<GlobalErrorResponse> handleInvalidToken(InvalidTokenException ex) {
        GlobalErrorResponse error = GlobalErrorResponse.of("INVALID_TOKEN", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error); // 401
    }
    //로그인 필요
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<GlobalErrorResponse> handleUnauthorized(UnauthorizedException ex) {
        GlobalErrorResponse error = GlobalErrorResponse.of("UNAUTHORIZED", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }
}