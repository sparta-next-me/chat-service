package org.nextme.chat_server.application.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.nextme.chat_server.domain.chatRoom.RoomType;

import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public record RoomSearchRequest(
        UUID userId,
        RoomType roomType

) {
}
