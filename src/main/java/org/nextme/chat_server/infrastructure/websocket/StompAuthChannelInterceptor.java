package org.nextme.chat_server.infrastructure.websocket;

import lombok.RequiredArgsConstructor;
import org.nextme.chat_server.infrastructure.security.JwtProperties;
import org.nextme.chat_server.infrastructure.security.JwtProvider;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * 웹소켓 전용 jwt 인증
 */
@RequiredArgsConstructor
public class StompAuthChannelInterceptor implements ChannelInterceptor {

    private final JwtProvider jwtProvider;

    // 클라이언트의 웹소켓 CONNECT 명령을 가로채 인증 수행
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {

        //message를 STOMP 전용 accessor로 감쌈
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        // STOMP 명령이 CONNECT 인지 확인
        if(StompCommand.CONNECT.equals(accessor.getCommand())) {


            //Authorization 헤더를 가져옴
            String token = accessor.getNativeHeader("Authorization").get(0);

            if(token == null || !token.startsWith("Bearer ")) {
                throw new RuntimeException("잘못된 토큰입니다.");
            }

            token = token.substring(7);

            // TODO : JWT토큰 구현되면 해금
            //UUID userId = jwtProvider.getUserId(token);
            UUID userId = UUID.randomUUID();
            String userName = "jwt 토큰 파싱해 얻은 사용자 이름을 넣거라";

            //세션에 UserId 저장
            accessor.getSessionAttributes().put("userId", userId);
            accessor.getSessionAttributes().put("userName", userName);
        }
        return message;
    }
}
