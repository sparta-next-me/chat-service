package org.nextme.chat_server.infrastructure.mybatis.mapper;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.nextme.chat_server.infrastructure.mybatis.dto.MessageHistoryDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
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

    // 안읽은 메세지 수를 조회하기위한 dto
    @Getter
    @Setter
    @NoArgsConstructor
    class UnreadCountQueryParam{
        private UUID chatRoomId;
        private LocalDateTime lastReadAt;
    }

    //여러 채팅방의 안 읽은 메세지 수를 가져오는 메서드
    List<Map<String, Object>> getUnreadCounts(
            @Param("params") List<UnreadCountQueryParam> params);
}
