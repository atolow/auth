package example.com.auth.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Auth API 문서")
                        .description("회원가입, 로그인, 권한 부여, 로그아웃 등 인증 관련 API 문서")
                        .version("v1.0.0"));
    }
}