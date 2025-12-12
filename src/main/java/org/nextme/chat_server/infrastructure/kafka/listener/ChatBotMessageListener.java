package org.nextme.chat_server.infrastructure.kafka.listener;

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

    @KafkaListener(
            topics = "ai.message",
            groupId = "ai-group",
            containerFactory = "kafkaListenerContainerFactory")
    public void listen(MessageTpl message,
                       @Header(KafkaHeaders.RECEIVED_KEY) String key) {

        ChatMessage om = (ChatMessage)message;
        log.info("받은 메세지 = {}", om);

        // 메세지 브로드 캐스팅
        chatBotService.listenChatbotMessage(
                ChatRoomId.of(om.getRoomId()),
                RoomType.AI,
                om.getContent(),
                om.getSessionId());
    }
}
