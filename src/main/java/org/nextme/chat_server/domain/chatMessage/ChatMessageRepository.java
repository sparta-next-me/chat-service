package org.nextme.chat_server.domain.chatMessage;

import org.nextme.chat_server.domain.chatRoom.ChatRoomId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, ChatMessageId> {
    void deleteByChatRoomId(ChatRoomId chatRoomId);
    //가장 최근 메세지 조회
    Optional<ChatMessage> findTopByChatRoomIdOrderByCreatedAtDesc(ChatRoomId chatRoomId);
}
