package example.com.auth.global.exception;

public class CustomUserNotFoundException extends RuntimeException {
    public CustomUserNotFoundException(String message) {
        super(message);
    }
}
