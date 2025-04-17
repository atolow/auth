package example.com.auth.global.exception;

import example.com.auth.global.dto.GlobalErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<GlobalErrorResponse> handleUserAlreadyExists(UserAlreadyExistsException ex) {
        GlobalErrorResponse error = GlobalErrorResponse.of("USER_ALREADY_EXISTS", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error); // 409
    }

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

    @ExceptionHandler(CustomUserNotFoundException.class)
    public ResponseEntity<GlobalErrorResponse> handleUserNotFoundException(CustomUserNotFoundException ex) {
        GlobalErrorResponse error = GlobalErrorResponse.of("USER_NOT_FOUND", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error); // 401
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<GlobalErrorResponse> handleInvalidToken(InvalidTokenException ex) {
        GlobalErrorResponse error = GlobalErrorResponse.of("INVALID_TOKEN", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error); // 401
    }

}