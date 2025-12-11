package org.nextme.chat_server.domain.chatMessage;

import org.nextme.chat_server.domain.chatRoom.ChatRoomId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, ChatMessageId> {
    void deleteByChatRoomId(ChatRoomId chatRoomId);
}
