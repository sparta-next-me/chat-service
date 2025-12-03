package org.nextme.chat_server.domain.chatMessage;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.util.UUID;

@ToString
@Getter
@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatMessageId {

    private UUID chatMessageId;

    protected ChatMessageId(UUID chatMessageId) { this.chatMessageId = chatMessageId; }

    public static ChatMessageId of(UUID chatMessageId) {
        return new ChatMessageId(chatMessageId);
    }

    public static ChatMessageId of() {
        return new ChatMessageId(UUID.randomUUID());
    }
}
