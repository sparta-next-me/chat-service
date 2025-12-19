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
import org.nextme.chat_server.domain.chatMessage.ChatMessageRepository;
import org.nextme.chat_server.domain.chatRoom.ChatRoom;
import org.nextme.chat_server.domain.chatRoom.ChatRoomId;
import org.nextme.chat_server.domain.chatRoom.ChatRoomRepository;
import org.nextme.chat_server.domain.chatRoom.RoomType;
import org.nextme.chat_server.domain.chatRoomMember.ChatRoomMember;
import org.nextme.chat_server.domain.chatRoomMember.ChatRoomMemberRepository;
import org.nextme.chat_server.domain.chatRoomMember.MemberStatus;
import org.nextme.chat_server.infrastructure.client.UserServiceClient;
import org.nextme.chat_server.infrastructure.mybatis.dto.ChatRoomWithLastMessageDto;
import org.nextme.chat_server.infrastructure.mybatis.mapper.ChatMessageQueryMapper;
import org.nextme.chat_server.infrastructure.mybatis.mapper.ChatRoomQueryMapper;
import org.nextme.common.security.UserPrincipal;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ChatRoomService {

    private final ChatRoomRepository roomRepository;
    private final ChatRoomMemberRepository roomMemberRepository;
    private final ChatMessageRepository messageRepository;
    private final ChatRoomQueryMapper chatRoomMapper;
    private final UserServiceClient userServiceClient;

    // 읽음 처리 구현
    private final RedisTemplate<String, Object> redisTemplate;
    private final ChatMessageQueryMapper chatMessageQueryMapper;
    private static final String LAST_MESSAGE_KEY_PREFIX = "chat_room:";
    private static final String LAST_MESSAGE_KEY_SUFFIX = ":last_message";


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
    public List<RoomSearchResponse> getChatRoomList(@AuthenticationPrincipal UserPrincipal principal,
                                                    RoomType roomType) {

        if (principal == null || roomType == null) {
            throw new ChatRoomException(ChatRoomErrorCode.CHAT_ROOM_REQUEST_EMPTY);
        }

        //로그인 사용자 매핑
        UUID userId = UUID.fromString(principal.userId());


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

        if (request == null || principal == null) {
            throw new ChatRoomException(ChatRoomErrorCode.CHAT_ROOM_CREATE_EMPTY);
        }

        //로그인 사용자 매핑
        UUID createUser = UUID.fromString(principal.userId());
        String createUserName = principal.getName();

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

                return RoomCreateResponse.from(newChatRoom);

            }catch(Exception e){
                throw new ChatRoomException(ChatRoomErrorCode.CHAT_ROOM_REQUEST_AI);
            }
            // 1:1 채팅방 생성
        }else if(RoomType.ADVICE.equals(request.roomType()) || RoomType.DIRECT.equals(request.roomType())){

            if (request.inviteUserId() == null || request.invitedUserName() == null || request.invitedUserName().isBlank()) {
                throw new ChatRoomException(ChatRoomErrorCode.CHAT_ROOM_CREATE_EMPTY, "1:1 채팅방 생성 시 초대할 사용자 정보가 필요합니다");
            }

            //전문가 채팅방생성 요청시 전문가 자격 검증
            if(RoomType.ADVICE.equals(request.roomType())
                    && principal.getRoles().stream().noneMatch(getRole -> (getRole.equals("ADVISOR")))){
                throw new ChatRoomException(ChatRoomErrorCode.CHAT_ROOM_ROLE_BAD);
            }

            try{
                String title = RoomType.DIRECT.equals(request.roomType())
                        ? String.format("%s님과 %s님의 대화", createUserName, request.invitedUserName())
                        : String.format("%s님의 %s님과의 상담", createUserName, request.invitedUserName());

                //1:1 채팅방 생성
                ChatRoom newChatRoom = ChatRoom.create(request.roomType(), title);
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
     * 채팅방 참여
     * @param
     * @return
     */
    public void joinChatRoom(@AuthenticationPrincipal UserPrincipal principal, ChatRoomId chatRoomId) {
        if (principal == null) {
            throw new ChatRoomException(ChatRoomErrorCode.CHAT_ROOM_JOIN_BAD);
        }

        UUID userId = UUID.fromString(principal.userId());

        //채팅 멤버 조회 후, 없으면 생성 후에 가져옴
        ChatRoomMember member = roomMemberRepository
                .findByChatRoomIdAndUserId(chatRoomId, userId)
                .orElseGet(()-> ChatRoomMember.join(chatRoomId, userId, principal.getName()));

        //채팅 나간 멤버라면 joined
        if(MemberStatus.LEFT.equals(member.getStatus())){
            member.join();
        }

        //마지막 메세지 읽음 처리
        messageRepository.findTopByChatRoomIdOrderByCreatedAtDesc(chatRoomId)
                .ifPresent(lastMessage -> {
                    member.updateLastReadMessage(lastMessage.getId());
                    log.info("메세지 읽음처리 로직");
                });

        roomMemberRepository.save(member);
    }

    /**
     * 채팅방 나가기
     *
     * @param
     * @return
     */
    public void leaveChatRoom(@AuthenticationPrincipal UserPrincipal principal, ChatRoomId chatRoomId){
        if (principal == null) {
            throw new ChatRoomException(ChatRoomErrorCode.CHAT_ROOM_LEAVE_BAD);
        }

        ChatRoomMember member = roomMemberRepository
                .findByChatRoomIdAndUserId(chatRoomId, UUID.fromString(principal.userId()))
                .orElseThrow(() -> new ChatRoomException(ChatRoomErrorCode.CHAT_ROOM_LEAVE_BAD, "해당 멤버가 해당 채팅방 소속이 아닙니다."));

        member.leave();
        roomMemberRepository.save(member);

        // 해당 채팅방에 참여중인 멤버 조회
        List<ChatRoomMember> members = roomMemberRepository
                .findByChatRoomIdAndStatus(chatRoomId, MemberStatus.JOINED);

        // 방을 나갔을 때 해당 방 멤버가 아무도 없으면
        if(members.isEmpty()){
            ChatRoom leaveRoom = roomRepository
                    .findById(chatRoomId)
                    .orElseThrow(() -> new ChatRoomException( ChatRoomErrorCode.CHAT_ROOM_NOT_FOUND));

            // 방 소프트 딜리트
            leaveRoom.markAsDeleted("system");
            roomRepository.save(leaveRoom);

            //해당 방의 메세지 전부 삭제
            messageRepository.deleteByChatRoomId(chatRoomId);

            //해당 방의 멤버 전부 삭제
            roomMemberRepository.deleteByChatRoomId(chatRoomId);
        }
    }

    /**
     * 채팅방 생성 리스너
     * @param
     * @return
     */
    public void listenChatRoomCreated(UUID advisorId, UUID userId, LocalDateTime reservationStartTime, LocalDateTime reservationEndTime, UUID reservationUserId) {

        if (advisorId == null || userId == null) {
            throw new ChatRoomException(ChatRoomErrorCode.CHAT_ROOM_CREATE_EMPTY, "전문가 상담 채팅방 생성 시 전문가와 사용자의 정보가 필요합니다");
        }
        String advisorName = userServiceClient.getUser(advisorId).name();
        String userName = userServiceClient.getUser(userId).name();


        try{
            String title = "";

            if(!advisorName.isEmpty() || !userName.isEmpty()){
                title = String.format("%s님의 %s님과의 상담", advisorName, userName);
            }else {
                title = "상담방";
            }

            //1:1 채팅방 생성
            ChatRoom newChatRoom = ChatRoom.create(RoomType.ADVICE, title);
            newChatRoom.ReservationChatRoom(reservationUserId, reservationStartTime, reservationEndTime);

            roomRepository.save(newChatRoom);

            //전문가 맴버 채팅방에 추가
            ChatRoomMember roomCreateMember = ChatRoomMember.join(newChatRoom.getId(), advisorId, advisorName);
            roomMemberRepository.save(roomCreateMember);

            //채팅방 초대받은 멤버
            ChatRoomMember roomInvitedMember = ChatRoomMember.join(newChatRoom.getId(), userId, userName);
            roomMemberRepository.save(roomInvitedMember);

        }catch(Exception e){
            throw new ChatRoomException(ChatRoomErrorCode.CHAT_ROOM_REQUEST_DIRECT);
        }
    }

    /**
     * 채팅방 예약 취소 리스너
     * @param
     * @return
     */
    public void listenChatRoomCancel(UUID reservationId) {
        ChatRoom chatRoom = roomRepository.findByReservationId(reservationId);

        if (chatRoom == null) {
            throw new ChatRoomException(ChatRoomErrorCode.CHAT_ROOM_NOT_FOUND);
        }

        chatRoom.markAsDeleted("system");
        roomRepository.save(chatRoom);
    }



    /**
     * 마지막 메세지 업데이트
     * @param
     * @return
     */
}
