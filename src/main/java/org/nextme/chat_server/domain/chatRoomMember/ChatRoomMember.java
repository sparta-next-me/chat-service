package org.nextme.chat_server.domain.chatRoomMember;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.SQLRestriction;
import org.nextme.chat_server.domain.chatMessage.ChatMessageId;
import org.nextme.chat_server.domain.chatRoom.ChatRoomId;
import org.nextme.common.jpa.BaseEntity;

import java.util.Objects;
import java.util.UUID;

@Getter
@Entity
@ToString
@Access(AccessType.FIELD)
@NoArgsConstructor
@Table(name="p_chat_room_member")
@SQLRestriction("deleted_at IS NULL")
public class ChatRoomMember extends BaseEntity {

    @EmbeddedId
    ChatRoomMemberId id; // 채팅방 참여자 ID

    @Embedded
    @Column(nullable=false)
    ChatRoomId chatRoomId; // 채팅방 ID

    @Column(nullable=false)
    UUID userId; // 사용자 ID

    @Column(nullable=false)
    String userName; //사용자 이름

    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    MemberRole role; // 역할

    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    MemberStatus status; // 참여상태

    @Embedded
    @AttributeOverride(name = "chatMessageId", column = @Column(name = "lastReadMessageId"))
    ChatMessageId lastReadMessageId; //마지막 읽은 메세지

    @Builder
    public ChatRoomMember(ChatRoomMemberId id, ChatRoomId chatRoomId, UUID userId, String userName, MemberRole role,  MemberStatus status, ChatMessageId lastReadMessageId) {
        this.id = Objects.requireNonNullElse(id, ChatRoomMemberId.of());
        this.chatRoomId = chatRoomId;
        this.userId = userId;
        this.userName = userName;
        this.role = role;
        this.status = status;
        this.lastReadMessageId = lastReadMessageId;
    }

    // 채팅방 참여
    public static ChatRoomMember join(ChatRoomId chatRoomId, UUID userId, String userName) {
        return ChatRoomMember.builder()
                .chatRoomId(chatRoomId)
                .userId(userId)
                .userName(userName)
                .role(MemberRole.MEMBER)
                .status(MemberStatus.JOINED)
                .build();
    }

    // 채팅방 퇴장
    public void leave(){
        this.status = MemberStatus.LEFT;
    }

    // 읽음 처리
    public void updateLastReadMessage(ChatMessageId messageId) {
        this.lastReadMessageId = messageId;
    }

    // 채팅방 참가 여부 체크
    public boolean isJoined(){
        return MemberStatus.JOINED.equals(this.status);
    }
}
