package org.nextme.chat_server.domain.chatMessage;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, ChatMessageId> {
}
