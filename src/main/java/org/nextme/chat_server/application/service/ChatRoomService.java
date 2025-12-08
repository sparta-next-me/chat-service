/**
 * @package     package org.nextme.chat_server.application.service
 * @class       ChatRoomService
 * @description 채팅 방 서비스
 *
 * @author      hakjun
 * @since       2025. 12. 5.
 * @version     1.0
 *
 * <pre>
 * == Modification Information ==
 * Date          Author        Description
 * ----------    -----------   ---------------------------
 * 2025. 12. 5.        hakjun       최초 생성
 * </pre>
 */

package org.nextme.chat_server.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.nextme.chat_server.application.dto.RoomSearchRequest;
import org.nextme.chat_server.application.dto.RoomSearchResponse;
import org.nextme.chat_server.application.exception.ChatRoomErrorCode;
import org.nextme.chat_server.application.exception.ChatRoomException;
import org.nextme.chat_server.domain.chatRoom.ChatRoom;
import org.nextme.chat_server.domain.chatRoom.ChatRoomRepository;
import org.nextme.chat_server.domain.chatRoom.RoomType;
import org.nextme.chat_server.domain.chatRoomMember.ChatRoomMemberRepository;
import org.nextme.chat_server.infrastructure.mybatis.dto.ChatRoomWithLastMessageDto;
import org.nextme.chat_server.infrastructure.mybatis.mapper.ChatRoomQueryMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ChatRoomService {

    private final ChatRoomRepository roomRepository;
    private final ChatRoomMemberRepository roomMemberRepository;
    private final ChatRoomQueryMapper chatRoomMapper;

    /**
     * DTO → Response 변환
     */
    private RoomSearchResponse toRoomSearchResponse(ChatRoomWithLastMessageDto dto) {
        return RoomSearchResponse.builder()
                .chatRoomId(dto.getChatRoomId())
                .title(dto.getTitle())
                .roomType(dto.getRoomType())
                .lastMessage(dto.getLastMessage())
                .build();
    }

    /**
     * 채팅방 리스트 조회
     * @param
     * @return
     */
    public List<RoomSearchResponse> getChatRoomList(RoomSearchRequest searchRequest) {

        if (searchRequest == null) {
            throw new ChatRoomException(ChatRoomErrorCode.CHAT_ROOM_REQUEST_EMPTY);
        }

        // 그룹 채팅방 조회
        if(RoomType.GROUP.equals(searchRequest.roomType())){

            List<ChatRoomWithLastMessageDto> dtoList = chatRoomMapper.findRoomsByTypeWithLastMessage(searchRequest.roomType());

            // dto 변환
            return dtoList.stream()
                    .map(this::toRoomSearchResponse)
                    .toList();

            //AI 채팅방 조회
        } else if(RoomType.AI.equals(searchRequest.roomType())){

            List<ChatRoomWithLastMessageDto> dtoList = chatRoomMapper.findRoomsByTypeAndUserWithLastMessage(searchRequest.userId(), searchRequest.roomType());

            // dto 변환
            return dtoList.stream()
                    .map(this::toRoomSearchResponse)
                    .toList();

            //1:1, 전문가 채팅방 조회
        } else if(RoomType.ADVICE.equals(searchRequest.roomType()) || RoomType.DIRECT.equals(searchRequest.roomType())){

            List<ChatRoomWithLastMessageDto> dtoList = chatRoomMapper.findRoomsByTypeAndUserWithLastMessage(searchRequest.userId(), searchRequest.roomType());

            // dto 변환
            return dtoList.stream()
                    .map(this::toRoomSearchResponse)
                    .toList();

        } else {
            throw new ChatRoomException(ChatRoomErrorCode.CHAT_ROOM_TYPE_BAD);
        }
    }

    /**
     * 채팅방 생성
     * 그룹 채팅방 생성, 1:1 채팅방 생성, 챗봇 채팅방 생성
     * 1:1, 챗봇 채팅방 생성 시, 사용자 JWT token의 userId 와 입력받은 userId
     * @param
     * @return
     */

    /**
     * 채팅방 나가기
     * @param
     * @return
     */


}
