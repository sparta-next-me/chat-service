package org.nextme.chat_server.application.dto;

import org.nextme.chat_server.domain.chatMessage.ChatMessage;
import org.nextme.chat_server.domain.chatMessage.ChatMessageId;
import org.nextme.chat_server.domain.chatRoom.ChatRoomId;

import java.util.UUID;

public record ChatMessageResponse(
        ChatMessageId messageId,
        ChatRoomId roomId,
        UUID senderId,
        String senderName,
        String content
) {
    public static ChatMessageResponse from(ChatMessage message) {
        return new ChatMessageResponse(
                message.getId(),
                message.getChatRoomId(),
                message.getSenderId(),
                message.getSenderName(),
                message.getContent()
        );
    }
}
