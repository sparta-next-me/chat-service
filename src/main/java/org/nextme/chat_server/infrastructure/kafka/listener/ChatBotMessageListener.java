package org.nextme.chat_server.infrastructure.kafka.listener;

import lombok.extern.slf4j.Slf4j;
import org.nextme.chat_server.infrastructure.kafka.dto.ChatMessage;
import org.nextme.chat_server.infrastructure.kafka.dto.MessageTpl;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ChatBotMessageListener {

    @KafkaListener(
            topics = "chat.message",
            groupId = "chat-group",
            containerFactory = "kafkaListenerContainerFactory")
    public void listen(MessageTpl message,
                       @Header(KafkaHeaders.RECEIVED_KEY) String key) {

        ChatMessage om = (ChatMessage)message;
        System.out.printf("수신 받은 메세지 key=%s, message=%s%n", key, om.getUserId().toString());
    }
}
