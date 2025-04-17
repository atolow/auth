package example.com.auth.utils;

import example.com.auth.security.UserDetailsImp;
import example.com.auth.user.domain.User;
import org.springframework.security.core.userdetails.UserDetails;

public class UserUtils {

    public static User getUser(UserDetails userDetails) {
        return ((UserDetailsImp) userDetails).getUser(); // ✅ 올바른 형변환
    }
}