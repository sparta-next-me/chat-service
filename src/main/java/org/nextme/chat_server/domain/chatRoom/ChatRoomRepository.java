package org.nextme.chat_server.domain.chatRoom;

import org.nextme.chat_server.domain.chatMessage.ChatMessage;
import org.nextme.chat_server.domain.chatMessage.ChatMessageId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, ChatRoomId> {
}
