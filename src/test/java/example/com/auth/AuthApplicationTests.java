package example.com.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import example.com.auth.security.JwtProvider;
import example.com.auth.user.domain.Role;
import example.com.auth.user.domain.User;
import example.com.auth.user.dto.LoginRequestDto;
import example.com.auth.user.dto.SignupRequestDto;
import example.com.auth.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
@SpringBootTest
@AutoConfigureMockMvc
class AuthApplicationTests {
	@Autowired private MockMvc mockMvc;
	@Autowired private ObjectMapper objectMapper;
	@Autowired private UserRepository userRepository;
	@Autowired private PasswordEncoder passwordEncoder;
	@Autowired private JwtProvider jwtProvider;


	@BeforeEach
	void cleanDatabase() {
		userRepository.deleteAll();
	}

	@Test
	@DisplayName("회원가입 성공")
	void signup_success() throws Exception {
		SignupRequestDto requestDto = new SignupRequestDto("test", "wow123", "TestNickname");
		String json = objectMapper.writeValueAsString(requestDto);

		mockMvc.perform(post("/auth/signup")
						.contentType(MediaType.APPLICATION_JSON)
						.content(json)) // 👈 여기가 문법적으로 문제 없음
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.username").value("test"))
				.andExpect(jsonPath("$.nickname").value("TestNickname"))
				.andExpect(jsonPath("$.role").value("USER"));
	}
	@Test
	@DisplayName("회원가입 실패 - 중복 사용자")
	void signup_duplicate_fail() throws Exception {
		userRepository.save(User.builder()
				.username("duplicateUser")
				.password(passwordEncoder.encode("wow123"))
				.nickname("DuplicateNickname")
				.role(Role.USER)
				.build());

		SignupRequestDto request = new SignupRequestDto("duplicateUser", "wow123", "TestNickname");

		mockMvc.perform(post("/auth/signup")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isConflict())
				.andExpect(jsonPath("$.error.code").value("USER_ALREADY_EXISTS"));
	}

	@Test
	@DisplayName("로그인 성공")
	void login_success() throws Exception {
		userRepository.save(User.builder()
				.username("loginUser")
				.password(passwordEncoder.encode("12341234"))
				.nickname("LoginTest")
				.role(Role.USER)
				.build());

		LoginRequestDto request = new LoginRequestDto("loginUser", "12341234");

		mockMvc.perform(post("/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.token").exists());
	}

	@Test
	@DisplayName("로그인 실패 - 잘못된 비밀번호")
	void login_fail_wrong_password() throws Exception {
		userRepository.save(User.builder()
				.username("wrongpw")
				.password(passwordEncoder.encode("realpass"))
				.nickname("WrongPW")
				.role(Role.USER)
				.build());

		LoginRequestDto request = new LoginRequestDto("wrongpw", "wrongpass");

		mockMvc.perform(post("/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isUnauthorized())
				.andExpect(jsonPath("$.error.code").value("INVALID_CREDENTIALS"));
	}

	@Test
	@DisplayName("관리자 권한 부여 - 성공")
	void grantAdmin_success() throws Exception {
		// ADMIN 유저 생성
		User admin = userRepository.save(User.builder()
				.username("admin")
				.password(passwordEncoder.encode("12345678"))
				.nickname("관리자")
				.role(Role.ADMIN)
				.build());

		// 일반 USER 생성
		User target = userRepository.save(User.builder()
				.username("targetUser")
				.password(passwordEncoder.encode("11112222"))
				.nickname("대상자")
				.role(Role.USER)
				.build());

		// 토큰 발급
		String token = jwtProvider.generateToken(admin.getUsername(), admin.getRole());

		mockMvc.perform(patch("/auth/users/" + target.getId() + "/roles")
						.header("Authorization", "Bearer " + token))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.username").value("targetUser"))
				.andExpect(jsonPath("$.role").value("ADMIN"));
	}

	@Test
	@DisplayName("일반 유저는 관리자 권한 부여 불가")
	void grantAdmin_fail_by_user() throws Exception {
		// 일반 USER 생성
		User user = userRepository.save(User.builder()
				.username("basicuser")
				.password(passwordEncoder.encode("userpass"))
				.nickname("유저")
				.role(Role.USER)
				.build());

		// 또 다른 유저 생성
		User target = userRepository.save(User.builder()
				.username("targetUser2")
				.password(passwordEncoder.encode("targetpass"))
				.nickname("타겟")
				.role(Role.USER)
				.build());

		// 토큰 발급
		String token = jwtProvider.generateToken(user.getUsername(), user.getRole());

		mockMvc.perform(patch("/auth/users/" + target.getId() + "/roles")
						.header("Authorization", "Bearer " + token))
				.andExpect(status().isForbidden())
				.andExpect(jsonPath("$.error.code").value("ACCESS_DENIED"));
	}


	@Test
	@DisplayName("존재하지 않는 사용자에게 관리자 권한 부여 시도")
	void grantAdmin_fail_user_not_found() throws Exception {
		// ADMIN 유저 생성
		User admin = userRepository.save(User.builder()
				.username("realadmin")
				.password(passwordEncoder.encode("adminpw"))
				.nickname("리얼관리자")
				.role(Role.ADMIN)
				.build());

		// 토큰 발급
		String token = jwtProvider.generateToken(admin.getUsername(), admin.getRole());

		mockMvc.perform(patch("/auth/users/9999/roles")
						.header("Authorization", "Bearer " + token))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.error.code").value("USER_NOT_FOUND"));
	}


	@Test
	@DisplayName("토큰 없이 관리자 권한 부여 시도 → 실패")
	void grantAdmin_fail_no_token() throws Exception {
		mockMvc.perform(patch("/auth/users/1/roles"))
				.andExpect(status().isUnauthorized());
	}

	@Test
	@DisplayName("잘못된 토큰 형식으로 관리자 권한 부여 시도 → 실패")
	void grantAdmin_fail_invalid_token() throws Exception {
		mockMvc.perform(patch("/auth/users/1/roles")
						.header("Authorization", "Bearer invalid.token.value"))
				.andExpect(status().isUnauthorized());
	}

	@Test
	@DisplayName("만료된 토큰으로 관리자 권한 부여 시도 → 실패")
	void grantAdmin_fail_expired_token() throws Exception {
		// 토큰의 유효 기간이 짧게 설정된 JwtProvider.generateToken 사용
		User admin = userRepository.save(User.builder()
				.username("expiredAdmin")
				.password(passwordEncoder.encode("12345678"))
				.nickname("만료된관리자")
				.role(Role.ADMIN)
				.build());

		// 1초 만에 만료되는 토큰 생성
		String expiredToken = jwtProvider.generateToken(admin.getUsername(), admin.getRole(), 1000); // 1초
		Thread.sleep(1500); // 1.5초 기다려서 토큰 만료시킴

		mockMvc.perform(patch("/auth/users/1/roles")
						.header("Authorization", "Bearer " + expiredToken))
				.andExpect(status().isUnauthorized())
				.andExpect(jsonPath("$.error.code").value("EXPIRED_JWT"));
	}
}

