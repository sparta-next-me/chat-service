package org.nextme.chat_server.domain.chatRoom;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.SQLRestriction;
import org.nextme.chat_server.domain.chatMessage.ChatMessageId;
import org.nextme.common.jpa.BaseEntity;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Getter
@Entity
@ToString
@Access(AccessType.FIELD)
@NoArgsConstructor
@Table(name="p_chat_room")
@SQLRestriction("deleted_at IS NULL")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChatRoom extends BaseEntity {

    @EmbeddedId
    ChatRoomId id; // 채팅방 ID

    String title; // 채팅방 이름

    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    RoomType roomType; // 채팅방 타입

    @Embedded
    @AttributeOverride(name = "chatMessageId", column= @Column(name = "lastMessageId"))
    ChatMessageId lastMessageId; // 마지막 메세지 (사용안함)

    LocalDateTime reservationStartTime; // 전문가 채팅방의 경우, 상담 시작 시간
    LocalDateTime reservationEndTime; // 전문가 채팅방의 경우, 상담 종료 시간

    UUID reservationId; // 전문가 채팅방의 경우, 예약 아이디


    @Builder
    public ChatRoom(ChatRoomId id, String title, RoomType roomType, ChatMessageId lastMessageId) {
        this.id = Objects.requireNonNullElse(id, ChatRoomId.of());
        this.title = title;
        this.roomType = roomType;
        this.lastMessageId = lastMessageId;
    }

    // 채팅방 생성
    public static ChatRoom create(RoomType roomType, String title) {
        return ChatRoom.builder()
                .roomType(roomType)
                .title(title)
                .build();
    }

    public void ReservationChatRoom(UUID reservationId, LocalDateTime reservationStartTime, LocalDateTime reservationEndTime) {
        this.reservationId = reservationId;
        this.reservationStartTime = reservationStartTime;
        this.reservationEndTime = reservationEndTime;
    }

    // 최근 메세지 업데이트
    public void updateLastMessage(ChatMessageId lastMessageId) {
        this.lastMessageId = lastMessageId;
    }
}
