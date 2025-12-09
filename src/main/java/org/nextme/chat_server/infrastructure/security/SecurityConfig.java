package org.nextme.chat_server.infrastructure.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;


/**
 * 테스트를 위한 보안 설정 해제
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                // CORS 설정 활성화
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .headers(headers -> headers.frameOptions(frame -> frame.disable()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/**").permitAll()
                        .anyRequest().permitAll()
                );
        return http.build();
    }

    // CORS 설정
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        // CORS 설정 객체 생성
        CorsConfiguration config = new CorsConfiguration();

        // 허용할 Origin(프론트엔드 주소)
        config.addAllowedOrigin("http://localhost:63342");

        // 허용할 HTTP 메서드
        config.addAllowedMethod("*"); // GET, POST, OPTIONS 등 모두 허용

        // 허용할 Header
        config.addAllowedHeader("*"); // Authorization, Content-Type 등

        // 인증정보(Cookie, Authorization) 포함 허용할지 여부
        config.setAllowCredentials(true);

        // 모든 경로에 이 설정을 적용
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }
}
