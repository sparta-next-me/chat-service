package org.nextme.chat_server.infrastructure.kafka.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.nextme.chat_server.application.service.ChatRoomService;
import org.nextme.chat_server.infrastructure.kafka.dto.ChatRoom;
import org.nextme.chat_server.infrastructure.kafka.dto.MessageTpl;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatRoomCreateListener {

    private final ChatRoomService chatRoomService;
    private final ObjectMapper objectMapper;

    @KafkaListener(
            topics = "chat.create",
            groupId = "chat-group")
    public void listen(String json,
                       @Header(KafkaHeaders.RECEIVED_TOPIC) String key) {

        try {
            ChatRoom om = objectMapper.readValue(json, ChatRoom.class);
            log.info("받은 메세지 = {}", om);

            chatRoomService.listenChatRoomCreated(
                    om.getAdvisorId(),
                    om.getUserId(),
                    om.getSDateTime(),
                    om.getEDateTime(),
                    om.getReservationId()
            );
        } catch (JsonProcessingException e) {
            log.error("ChatRoom 변환 오류: {}", e.getMessage(), e);
        }
    }
}
