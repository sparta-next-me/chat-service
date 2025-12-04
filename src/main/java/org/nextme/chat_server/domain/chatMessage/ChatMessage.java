package org.nextme.chat_server.domain.chatMessage;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.SQLRestriction;
import org.nextme.chat_server.domain.chatRoom.ChatRoomId;
import org.nextme.common.jpa.BaseEntity;

import java.util.Objects;
import java.util.UUID;

@Getter
@Entity
@ToString
@Access(AccessType.FIELD)
@NoArgsConstructor
@Table(name="p_chat_message")
@SQLRestriction("deleted_at IS NULL")
public class ChatMessage extends BaseEntity {

    @EmbeddedId
    ChatMessageId id; // 메세지 ID

    @Embedded
    @Column(nullable=false)
    ChatRoomId chatRoomId; // 채팅방 ID

    @Column(nullable=false)
    UUID senderId; // 전송자 ID

    @Column(nullable=false, length=255)
    String content; // 메세지 내용

    @Builder
    public ChatMessage(ChatMessageId id, ChatRoomId chatRoomId, UUID senderId, String content) {
        this.id = Objects.requireNonNullElse(id,ChatMessageId.of());
        this.chatRoomId = chatRoomId;
        this.senderId = senderId;
        this.content = content;
    }

    // 메세지 생성
    public static ChatMessage create(ChatRoomId chatRoomId, UUID senderId, String content) {
        validate(content);

        return ChatMessage.builder()
                .chatRoomId(chatRoomId)
                .senderId(senderId)
                .content(content)
                .build();
    }

    // 메세지 검증
    private static void validate(String content) {
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("메세지 내용이 비어있습니다.");
        }
        if (content.length() > 255) {
            throw new IllegalArgumentException("메세지는 255자를 초과할 수 없습니다.");
        }
    }

}
