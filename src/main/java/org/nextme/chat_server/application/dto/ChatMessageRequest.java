package org.nextme.chat_server.application.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.nextme.chat_server.domain.chatRoom.RoomType;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ChatMessageRequest(
        String content,
        RoomType roomType
) {}
