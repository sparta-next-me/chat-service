package org.nextme.chat_server.infrastructure.kafka.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage implements MessageTpl{
    private UUID roomId;
    private String roomType;
    private String content;
    private String sessionId;
}
