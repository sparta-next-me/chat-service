package org.nextme.chat_server.infrastructure.websocket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.nextme.chat_server.application.service.ChatBotService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketEventListener {

    private final ChatBotService chatBotService;

    /**
     * 웹소켓 종료 이벤트 리스너
     */
    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        String sessionId = event.getSessionId();
        log.info("웹소켓 연결 종료 - sessionId: {}", sessionId);

        chatBotService.clearHistory(sessionId);
    }
}
