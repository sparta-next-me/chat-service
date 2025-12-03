package org.nextme.chat_server.domain.chatRoomMember;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.util.UUID;

@ToString
@Getter
@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoomMemberId {

    private UUID chatRoomMemberId;

    protected ChatRoomMemberId(UUID chatRoomMemberId) {
        this.chatRoomMemberId = chatRoomMemberId;
    }

    public static ChatRoomMemberId of(UUID chatRoomMemberId) {
        return new ChatRoomMemberId(chatRoomMemberId);
    }

    public static ChatRoomMemberId of() {
        return new ChatRoomMemberId(UUID.randomUUID());
    }
}
