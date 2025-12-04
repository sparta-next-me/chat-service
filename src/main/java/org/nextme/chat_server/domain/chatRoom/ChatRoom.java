package org.nextme.chat_server.domain.chatRoom;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.SQLRestriction;
import org.nextme.chat_server.domain.chatMessage.ChatMessageId;
import org.nextme.common.jpa.BaseEntity;

import java.util.Objects;
import java.util.UUID;

@Getter
@Entity
@ToString
@Access(AccessType.FIELD)
@NoArgsConstructor
@Table(name="p_chat_room")
@SQLRestriction("deleted_at IS NULL")
public class ChatRoom extends BaseEntity {

    @EmbeddedId
    ChatRoomId id; // 채팅방 ID

    String title; // 채팅방 이름

    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    RoomType roomType; // 채팅방 타입

    @Embedded
    @AttributeOverride(name = "chatMessageId", column= @Column(name = "lastMessageId"))
    ChatMessageId lastMessageId; // 최근메세지

    @Builder
    public ChatRoom(ChatRoomId id, String title, RoomType roomType, ChatMessageId lastMessageId) {
        this.id = Objects.requireNonNullElse(id, ChatRoomId.of());
        this.title = title;
        this.roomType = roomType;
        this.lastMessageId = lastMessageId;
    }

}
