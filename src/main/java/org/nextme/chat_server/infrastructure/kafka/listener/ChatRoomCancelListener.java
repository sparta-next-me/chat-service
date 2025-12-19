package org.nextme.chat_server.infrastructure.kafka.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.nextme.chat_server.application.service.ChatRoomService;
import org.nextme.chat_server.infrastructure.kafka.dto.ChatRoomCancel;
import org.nextme.chat_server.infrastructure.kafka.dto.MessageTpl;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatRoomCancelListener {

    private final ChatRoomService chatRoomService;
    private final ObjectMapper om;

    @KafkaListener(
            topics = "chat.cancel",
            groupId = "chat-group")
    public void Listen(String json,
                       @Header(KafkaHeaders.RECEIVED_TOPIC) String key) {

        try {
            ChatRoomCancel message = om.readValue(json, ChatRoomCancel.class);

            log.info("받은 메세지 = {}", message);
        } catch (JsonProcessingException e) {
            log.error("ChatRoomCancel 변환 오류: {}", e.getMessage(), e);
        }
    }
}
