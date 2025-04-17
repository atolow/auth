package example.com.auth.user.controller;


import example.com.auth.security.UserDetailsImp;
import example.com.auth.user.domain.User;
import example.com.auth.user.dto.LoginRequestDto;
import example.com.auth.user.dto.LoginResponseDto;
import example.com.auth.user.dto.SignupRequestDto;
import example.com.auth.user.dto.UserResponseDto;
import example.com.auth.user.service.UserService;
import example.com.auth.utils.UserUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class UserController {
    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<UserResponseDto> creatUser(
            @RequestBody SignupRequestDto requestDto) {
        UserResponseDto userResponseDto = userService.signup(requestDto);

        return new ResponseEntity<>(userResponseDto, HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto request) {
        LoginResponseDto response = userService.login(request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/users/{userId}/roles")
    public ResponseEntity<UserResponseDto> grantAdmin(@PathVariable Long userId,
                                                      @AuthenticationPrincipal UserDetailsImp userDetails) {

        User user = UserUtils.getUser(userDetails);
        UserResponseDto updatedUser = userService.grantAdminRole(userId, user);
        return ResponseEntity.ok(updatedUser);
    }

}
