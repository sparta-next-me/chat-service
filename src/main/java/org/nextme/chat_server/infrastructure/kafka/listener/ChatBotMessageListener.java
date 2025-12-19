package org.nextme.chat_server.infrastructure.kafka.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.nextme.chat_server.application.service.ChatBotService;
import org.nextme.chat_server.domain.chatRoom.ChatRoomId;
import org.nextme.chat_server.domain.chatRoom.RoomType;
import org.nextme.chat_server.infrastructure.kafka.dto.ChatMessage;
import org.nextme.chat_server.infrastructure.kafka.dto.MessageTpl;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatBotMessageListener {

    private final ChatBotService chatBotService;
    private final ObjectMapper om;

    @KafkaListener(
            topics = "ai.message",
            groupId = "ai-group")
    public void listen(String json,
                       @Header(KafkaHeaders.RECEIVED_KEY) String key) {
        try {
            ChatMessage message = om.readValue(json, ChatMessage.class);
            log.info("받은 메세지 = {}", message);

            // 메세지 브로드 캐스팅
            chatBotService.listenChatbotMessage(
                    ChatRoomId.of(message.getRoomId()),
                    RoomType.AI,
                    message.getContent(),
                    message.getSessionId());
        } catch (JsonProcessingException e) {
            log.error("ChatMessage 변환 오류: {}", e.getMessage(), e);
        }
    }
}
