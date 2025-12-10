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
import org.nextme.chat_server.application.dto.RoomCreateRequest;
import org.nextme.chat_server.application.dto.RoomCreateResponse;
import org.nextme.chat_server.application.dto.RoomSearchResponse;
import org.nextme.chat_server.application.exception.ChatRoomErrorCode;
import org.nextme.chat_server.application.exception.ChatRoomException;
import org.nextme.chat_server.domain.chatRoom.ChatRoom;
import org.nextme.chat_server.domain.chatRoom.ChatRoomRepository;
import org.nextme.chat_server.domain.chatRoom.RoomType;
import org.nextme.chat_server.domain.chatRoomMember.ChatRoomMember;
import org.nextme.chat_server.domain.chatRoomMember.ChatRoomMemberRepository;
import org.nextme.chat_server.infrastructure.mybatis.dto.ChatRoomWithLastMessageDto;
import org.nextme.chat_server.infrastructure.mybatis.mapper.ChatRoomQueryMapper;
import org.nextme.common.security.UserPrincipal;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

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
    public List<RoomSearchResponse> getChatRoomList(UUID userId, RoomType roomType) {

        if (userId == null || roomType == null) {
            throw new ChatRoomException(ChatRoomErrorCode.CHAT_ROOM_REQUEST_EMPTY);
        }

        // 그룹 채팅방 조회
        if(RoomType.GROUP.equals(roomType)){

            List<ChatRoomWithLastMessageDto> dtoList = chatRoomMapper.findRoomsByTypeWithLastMessage(roomType);

            // dto 변환
            return dtoList.stream()
                    .map(this::toRoomSearchResponse)
                    .toList();

            //AI 채팅방 조회
        } else if(RoomType.AI.equals(roomType)){

            List<ChatRoomWithLastMessageDto> dtoList = chatRoomMapper.findRoomsByTypeAndUserWithLastMessage(userId, roomType);

            // dto 변환
            return dtoList.stream()
                    .map(this::toRoomSearchResponse)
                    .toList();

            //1:1, 전문가 채팅방 조회
        } else if(RoomType.ADVICE.equals(roomType) || RoomType.DIRECT.equals(roomType)){

            List<ChatRoomWithLastMessageDto> dtoList = chatRoomMapper.findRoomsByTypeAndUserWithLastMessage(userId, roomType);

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
     * 1:1, 챗봇 채팅방 생성 시, 사용자 JWT token의 userId 와 입력받은 userId로 생성
     * 챗봇은 userId 에 챗봇용 userId 입력
     * 그룹 생성은
     * @param
     * @return
     */
    public RoomCreateResponse createChatRoom(@AuthenticationPrincipal UserPrincipal principal, RoomCreateRequest request) {

        if (request == null) {
            throw new ChatRoomException(ChatRoomErrorCode.CHAT_ROOM_CREATE_EMPTY);
        }

        //TODO: JWT 토큰에서 채팅방 생성 유저ID 가져오기
//        UUID createUser = UUID.randomUUID();
//        String createUserName = "로그인한 사용자";

        String createUserName = principal.getName();
        UUID createUser = UUID.fromString(principal.userId());

        // 그룹 채팅방 생성
        if(RoomType.GROUP.equals(request.roomType())){
            try{
                // 채팅방 생성
                ChatRoom newChatRoom = ChatRoom.create(request.roomType(), request.title().isBlank() ? "그룹 채팅방" : request.title());
                roomRepository.save(newChatRoom);

                //채팅 멤버 생성
                ChatRoomMember createRoomMember = ChatRoomMember.join(newChatRoom.getId(), createUser, createUserName);
                roomMemberRepository.save(createRoomMember);

                return RoomCreateResponse.from(newChatRoom);

            }catch(Exception e){
                throw new ChatRoomException(ChatRoomErrorCode.CHAT_ROOM_REQUEST_GROUP);
            }
            // AI 채팅방 생성
        }else if(RoomType.AI.equals(request.roomType())){

            try{
                //챗봇 채팅방 생성
                ChatRoom newChatRoom = ChatRoom.create(request.roomType(),"챗봇 채팅방");
                roomRepository.save(newChatRoom);

                //채팅 멤버 생성
                ChatRoomMember chatRoomMember = ChatRoomMember.join(newChatRoom.getId(), createUser, createUserName);
                roomMemberRepository.save(chatRoomMember);

                // TODO : 채팅 봇 생성?

                return RoomCreateResponse.from(newChatRoom);

            }catch(Exception e){
                throw new ChatRoomException(ChatRoomErrorCode.CHAT_ROOM_REQUEST_AI);
            }
            // 1:1 채팅방 생성
        }else if(RoomType.ADVICE.equals(request.roomType()) || RoomType.DIRECT.equals(request.roomType())){
            try{
                //1:1 채팅방 생성
                ChatRoom newChatRoom = ChatRoom.create(request.roomType(), request.invitedUserName());
                roomRepository.save(newChatRoom);

                //채팅방 만든 멤버
                ChatRoomMember roomCreateMember = ChatRoomMember.join(newChatRoom.getId(), createUser, createUserName);
                roomMemberRepository.save(roomCreateMember);

                //채팅방 초대받은 멤버
                ChatRoomMember roomInvitedMember = ChatRoomMember.join(newChatRoom.getId(), request.inviteUserId(), request.invitedUserName());
                roomMemberRepository.save(roomInvitedMember);

                return RoomCreateResponse.from(newChatRoom);

            }catch(Exception e){
                throw new ChatRoomException(ChatRoomErrorCode.CHAT_ROOM_REQUEST_DIRECT);
            }
        }else{
            throw new ChatRoomException(ChatRoomErrorCode.CHAT_ROOM_CREATE_EMPTY, "채팅방 생성시 방 타입을 지정해 주세요");

        }
    }
    
    /**
     * 마지막 메세지 업데이트
     * @param  
     * @return 
     */

    /**
     * 채팅방 참여
     * @param
     * @return
     */

    /**
     * 채팅방 나가기
     * @param
     * @return
     */


}
