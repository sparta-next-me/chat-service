package org.nextme.chat_server.domain.chatRoom;

import org.nextme.chat_server.application.dto.RoomSearchResponse;
import org.nextme.chat_server.domain.chatMessage.ChatMessage;
import org.nextme.chat_server.domain.chatMessage.ChatMessageId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, ChatRoomId> {
    List<ChatRoom> findAllByRoomType(RoomType roomType);
    ChatRoom findByReservationId(UUID reservationId);


}
