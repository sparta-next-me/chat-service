package org.nextme.chat_server.application.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@RedisHash("last-message")
@JsonIgnoreProperties(ignoreUnknown = true)
public record LastMessageCacheDto(
        @Id
    UUID messageId,
    String content,
    UUID senderId,
    String senderName,
    LocalDateTime createdAt
) implements Serializable {}
