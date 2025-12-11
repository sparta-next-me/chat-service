package org.nextme.chat_server.infrastructure.mybatis.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.nextme.chat_server.domain.chatRoom.RoomType;

import java.util.UUID;

@Getter
@Setter
@ToString
public class ChatRoomWithLastMessageDto {

    private UUID chatRoomId;
    private String title;
    private RoomType roomType;
    private String lastMessage;
}
