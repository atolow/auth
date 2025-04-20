package example.com.auth.utils;

import example.com.auth.global.exception.UnauthorizedException;
import example.com.auth.security.UserDetailsImp;
import example.com.auth.user.domain.User;
import org.springframework.security.core.userdetails.UserDetails;

public class UserUtils {

    public static User getUser(UserDetails userDetails) {
        if (userDetails == null) {
            throw new UnauthorizedException("아이디가 존재하지 않습니다.");
        }

        return ((UserDetailsImp) userDetails).getUser(); // ✅ 형변환은 유지
    }
}