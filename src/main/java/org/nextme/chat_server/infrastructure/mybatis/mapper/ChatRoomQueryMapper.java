package org.nextme.chat_server.infrastructure.mybatis.mapper;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.nextme.chat_server.domain.chatRoom.RoomType;
import org.nextme.chat_server.infrastructure.mybatis.dto.ChatRoomWithLastMessageDto;

import java.util.List;
import java.util.UUID;

@Mapper
public interface ChatRoomQueryMapper {

    /**
     * 특정 타입의 채팅방 목록 조회 (마지막 메시지 포함)
     */
    List<ChatRoomWithLastMessageDto> findRoomsByTypeWithLastMessage(
            @Param("roomType") RoomType roomType
    );

    /**
     * 특정 타입의 채팅방, 사용자가 속한 채팅방 목록 조회 (마지막 메시지 포함)
     */
    List<ChatRoomWithLastMessageDto> findRoomsByTypeAndUserWithLastMessage(
            @Param("userId") UUID userId,
            @Param("roomType") RoomType roomType
    );
}
