package org.nextme.chat_server.infrastructure.security;

import lombok.RequiredArgsConstructor;
import org.nextme.common.jwt.JwtTokenProvider;
import org.nextme.common.jwt.TokenBlacklistService;
import org.nextme.common.security.DirectJwtAuthenticationFilter;
import org.nextme.common.security.GatewayUserHeaderAuthenticationFilter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;


/**
 * 테스트를 위한 보안 설정 해제
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity(prePostEnabled = true)
@EnableConfigurationProperties(JwtProperties.class)
public class SecurityConfig {

    @Bean
    public GatewayUserHeaderAuthenticationFilter gatewayUserHeaderAuthenticationFilter() {
        return new GatewayUserHeaderAuthenticationFilter();
    }

    @Bean
    public DirectJwtAuthenticationFilter directJwtAuthenticationFilter(
            JwtTokenProvider jwtTokenProvider,
            TokenBlacklistService tokenBlacklistService
    ) {
        return new DirectJwtAuthenticationFilter(
                jwtTokenProvider,
                tokenBlacklistService,
                List.of()
        );
    }


    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            GatewayUserHeaderAuthenticationFilter gatewayUserHeaderAuthenticationFilter,
            DirectJwtAuthenticationFilter directJwtAuthenticationFilter
    ) throws Exception {

        http
                // CORS 설정 활성화
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .formLogin(form -> form.disable())
                .headers(headers -> headers.frameOptions(frame -> frame.disable()))
                .httpBasic(basic -> basic.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/health", "/public/**").permitAll()
                        .requestMatchers("/v1/advisor/auth/**").permitAll()
                        .requestMatchers("/**").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(gatewayUserHeaderAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(directJwtAuthenticationFilter,
                        GatewayUserHeaderAuthenticationFilter.class);

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
