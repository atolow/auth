package example.com.auth.user.service;

import example.com.auth.user.domain.User;
import example.com.auth.user.dto.LoginRequestDto;
import example.com.auth.user.dto.LoginResponseDto;
import example.com.auth.user.dto.SignupRequestDto;
import example.com.auth.user.dto.UserResponseDto;

public interface UserService {
    UserResponseDto userSignup(SignupRequestDto request);
    UserResponseDto adminSignup(SignupRequestDto request);
    LoginResponseDto login(LoginRequestDto request);
    UserResponseDto grantAdminRole(Long userId, User userDetails);
}
