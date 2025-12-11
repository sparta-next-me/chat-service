package org.nextme.chat_server.application.dto;

import lombok.Builder;
import org.nextme.chat_server.domain.chatRoom.ChatRoom;
import org.nextme.chat_server.domain.chatRoom.RoomType;

import java.util.UUID;

@Builder
public record RoomSearchResponse(
    UUID chatRoomId,
    String title,
    RoomType roomType,
    String lastMessage
) {
    public static
    RoomSearchResponse from(ChatRoom chatRoom, String lastMessage) {
        return new RoomSearchResponse(
                chatRoom.getId().getChatRoomId(),
                chatRoom.getTitle(),
                chatRoom.getRoomType(),
                lastMessage
        );
    }
}
