package org.nextme.chat_server.application.dto;

import lombok.Builder;
import org.nextme.chat_server.domain.chatMessage.ChatMessage;
import org.nextme.chat_server.domain.chatMessage.ChatMessageId;
import org.nextme.chat_server.domain.chatRoom.ChatRoomId;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record ChatMessageResponse(
        ChatMessageId messageId,
        ChatRoomId roomId,
        UUID senderId,
        String senderName,
        String content,
        LocalDateTime createdAt
) {
    public static ChatMessageResponse from(ChatMessage message) {
        return new ChatMessageResponse(
                message.getId(),
                message.getChatRoomId(),
                message.getSenderId(),
                message.getSenderName(),
                message.getContent(),
                message.getCreatedAt()
        );
    }
}
