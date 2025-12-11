package org.nextme.chat_server.application.dto;

import lombok.Builder;
import org.nextme.chat_server.domain.chatRoom.ChatRoom;
import org.nextme.chat_server.domain.chatRoom.RoomType;

import java.util.UUID;

@Builder
public record RoomCreateResponse(
        UUID chatRoomId,
        String title,
        RoomType roomType
) {
    public static
    RoomCreateResponse from(ChatRoom chatRoom) {
        return new RoomCreateResponse(
                chatRoom.getId().getChatRoomId(),
                chatRoom.getTitle(),
                chatRoom.getRoomType()
        );
    }
}

