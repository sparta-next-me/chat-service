package org.nextme.chat_server.domain.chatRoom;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.util.UUID;

@ToString
@Getter
@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoomId {

    private UUID chatRoomId;

    protected ChatRoomId(UUID chatRoomId) {
        this.chatRoomId = chatRoomId;
    }

    public static ChatRoomId of(UUID chatRoomId) {
        return new ChatRoomId(chatRoomId);
    }

    public static ChatRoomId of() {
        return new ChatRoomId(UUID.randomUUID());
    }

}
