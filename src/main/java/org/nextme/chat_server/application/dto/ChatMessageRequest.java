package org.nextme.chat_server.application.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.nextme.chat_server.domain.chatRoom.RoomType;

public record ChatMessageRequest(
        String content,
        RoomType roomType
) {}
