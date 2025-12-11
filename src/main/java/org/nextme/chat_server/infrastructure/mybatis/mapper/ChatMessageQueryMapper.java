package org.nextme.chat_server.infrastructure.mybatis.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.nextme.chat_server.infrastructure.mybatis.dto.MessageHistoryDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Mapper
public interface ChatMessageQueryMapper {

    /**
     * 메세지 히스토리 조회(스크롤)
     * @param
     * @return
     */
    List<MessageHistoryDto> findMessageHistory(
            @Param("chatRoomId") UUID chatRoomId,
            @Param("beforeMessageId") UUID beforeMessageId,
            @Param("beforeCreatedAt") LocalDateTime beforeCreatedAt,
            @Param("size") int size
    );
}
