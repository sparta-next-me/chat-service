package org.nextme.chat_server.domain.chatRoomMember;

import org.nextme.chat_server.domain.chatRoom.ChatRoomId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChatRoomMemberRepository extends JpaRepository<ChatRoomMember, ChatRoomMemberId> {

    Optional<ChatRoomMember> findByChatRoomIdAndUserId(ChatRoomId chatRoomId, UUID userId);
    boolean existsByChatRoomIdAndUserId(ChatRoomId chatRoomId, UUID userId);

    List<ChatRoomMember> findByChatRoomIdAndStatus(ChatRoomId chatRoomId, MemberStatus status);
    void deleteByChatRoomId(ChatRoomId chatRoomId);

}
