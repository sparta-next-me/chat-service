package org.nextme.chat_server.application.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

public record LastMessageCacheDto(
    UUID messageId,
    String content,
    UUID senderId,
    String senderName,
    LocalDateTime createdAt
) implements Serializable {
}
