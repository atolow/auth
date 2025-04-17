package example.com.auth.user.dto;


import example.com.auth.user.domain.Role;
import example.com.auth.user.domain.User;
import lombok.Builder;
import lombok.Getter;

import java.util.Set;

@Getter
public class UserResponseDto {
    private final Long id;
    private final String username;
    private final String nickname;
    private final Role role;

    @Builder
    public UserResponseDto(Long id, String username, String nickname, Role role) {
        this.id = id;
        this.username = username;
        this.nickname = nickname;
        this.role = role;
    }



    public static UserResponseDto from(User user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .role(user.getRole())
                .build();
    }
}