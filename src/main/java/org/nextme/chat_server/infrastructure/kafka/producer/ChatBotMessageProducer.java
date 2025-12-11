package org.nextme.chat_server.infrastructure.kafka.producer;

import org.nextme.chat_server.application.service.MessageProducer;
import org.nextme.chat_server.infrastructure.kafka.dto.ChatMessage;
import org.nextme.chat_server.infrastructure.kafka.dto.MessageTpl;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ChatBotMessageProducer implements MessageProducer {

    private final KafkaTemplate<String, MessageTpl> kafkaTemplate;

    public ChatBotMessageProducer(KafkaTemplate<String, MessageTpl> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void send(UUID userId) {
        kafkaTemplate.send("chat.message", "chatBot", new ChatMessage(userId));
    }
}
