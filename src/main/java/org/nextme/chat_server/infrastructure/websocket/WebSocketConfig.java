package org.nextme.chat_server.infrastructure.websocket;

import lombok.RequiredArgsConstructor;
import org.nextme.chat_server.infrastructure.security.JwtProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * STOMP 기반 WebSocket 설정
 */
@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtProvider jwtProvider;

    @Bean
    public StompAuthChannelInterceptor stompAuthChannelInterceptor(){
        return new StompAuthChannelInterceptor(jwtProvider); // jwtProvider 주입
    }

    // WebSocket endpoint 등록, SockJS 지원(ws 연결 불가할시 대안)
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        //웹소켓 접속을 받는 엔드포인트 생성
        //todo: 실 운영 환경에서는 해당 옵션 제거 setAllowedOriginPatterns("*") (cors 보안정책 회피)
        registry.addEndpoint("/ws/chat")
                .setAllowedOriginPatterns("*") // 와일드카드 패턴 허용
                .withSockJS();
    }

    // 메세지 브로커 설정
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic"); // 클라이언트 구독 prefix
        registry.setApplicationDestinationPrefixes("/app"); // 클라이언트 전송 prefix
    }

    // 웹소켓 인터셉터 등록
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(stompAuthChannelInterceptor());
    }
}
