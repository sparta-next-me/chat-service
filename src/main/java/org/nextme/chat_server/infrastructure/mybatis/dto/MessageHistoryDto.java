package org.nextme.chat_server.infrastructure.mybatis.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@ToString(exclude = "content")
public class MessageHistoryDto {
    private UUID chatMessageId;
    private UUID chatRoomId;
    private UUID senderId;
    private String senderName;
    private String content;
    private LocalDateTime createdAt;
}
