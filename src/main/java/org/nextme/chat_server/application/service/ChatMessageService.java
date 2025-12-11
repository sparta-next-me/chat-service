/**
 * @package     org.nextme.chat_server.application.service
 * @class       ChatMessageService
 * @description 흐으후루꾸꾸루후으흐흐으후루꾸꾸루후으흐흐으후루꾸꾸루후으흐흐으후루꾸꾸루후으흐흐으후루꾸꾸루후으흐흐으후루꾸꾸루후으흐흐으후루꾸꾸루후으흐흐으후루꾸꾸루후으흐흐으후루꾸꾸루후으흐흐으후루꾸꾸루후으흐흐으후루꾸꾸루후으흐흐으후루꾸꾸루후으흐흐으후루꾸꾸루후으흐흐으후루꾸꾸루후으흐
 *
 * TODO: 트러블 슈팅 내역 -> 메세지 한번당 값 검증과 마지막 메세지 저장을 처리하면 반드시 문제가 생기므로 레디스 사용해서 성능 증진
 *
 * @author      hakjun
 * @since       2025. 12. 4.
 * @version     1.0
 *
 * <pre>
 * == Modification Information ==
 * Date          Author        Description
 * ----------    -----------   ---------------------------
 * 2025. 12. 4.        hakjun       최초 생성
 * </pre>
 */
package org.nextme.chat_server.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.nextme.chat_server.application.dto.ChatMessageRequest;
import org.nextme.chat_server.application.dto.ChatMessageResponse;
import org.nextme.chat_server.domain.chatMessage.ChatMessage;
import org.nextme.chat_server.domain.chatMessage.ChatMessageId;
import org.nextme.chat_server.domain.chatMessage.ChatMessageRepository;
import org.nextme.chat_server.domain.chatRoom.ChatRoomId;
import org.nextme.chat_server.domain.chatRoom.ChatRoomRepository;
import org.nextme.chat_server.domain.chatRoom.RoomType;
import org.nextme.chat_server.domain.chatRoomMember.ChatRoomMemberRepository;
import org.nextme.chat_server.infrastructure.mybatis.dto.MessageHistoryDto;
import org.nextme.chat_server.infrastructure.mybatis.mapper.ChatMessageQueryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatMessageQueryMapper chatMessageMapper;
    private final SimpMessagingTemplate simpMessagingTemplate;
    MessageProducer producer;

    /**
     * DTO → Response 변환
     * @param
     * @return
     */
    private ChatMessageResponse toChatMessageResponse(MessageHistoryDto dto) {
        return ChatMessageResponse.builder()
                .messageId(ChatMessageId.of(dto.getChatMessageId()))
                .roomId(ChatRoomId.of(dto.getChatRoomId()))
                .senderId(dto.getSenderId())
                .senderName(dto.getSenderName())
                .content(dto.getContent())
                .createdAt(dto.getCreatedAt())
                .build();
    }

    /**
     * 채팅 메세지 전송
     * @param
     * @return
     */
    public Void sendMessage(ChatRoomId roomId, UUID senderId, String senderName, ChatMessageRequest request) {
        log.info("메세지 전송 - roomId = {}, senderId = {}, senderName = {}, request = {}"
                , roomId, senderId, senderName ,request);

        //TODO: 사용자 값 검증 캐싱해서 확인

        //메세지 생성
        ChatMessage message = ChatMessage.create(
                roomId,
                senderId,
                senderName,
                request.content()
        );

        //메세지 저장
        ChatMessageResponse response = ChatMessageResponse
                .from(chatMessageRepository.save(message));

        //TODO: 방 최근 메세지 redis 업데이트

        // 방 타입이 챗봇일 경우 분기
        if(request.roomType() != null && RoomType.AI.equals(request.roomType())){
            producer.send(senderId);
            System.out.println("챗봇 로직 이벤트 처리");
            System.out.println("챗봇 비동기 메세지 전송");
        }

        try {
            // 방 구독자들에게 메세지 브로드 캐스트
            simpMessagingTemplate.convertAndSend(
                    "/topic/chat.room." + roomId.getChatRoomId(),
                    response);

            log.info("메세지 브로드캐스트 완료 - {}", response.messageId());

        }catch (Exception e){

            log.info("메세지 전송 실패 - {}", roomId, e);

            // 세션에 userId 가 있는 경우에만 에러 전송
            if (senderId != null) {
                // 발신자에게 에러 전송
                simpMessagingTemplate.convertAndSendToUser(
                        senderId.toString(),
                        "/queue/errors",
                        Map.of("error", e.getMessage())
                );
            }
        }

        return null;
    }

    /**
     * 채팅방 메세지 히스토리 조회
     * @param
     * @return
     */
    public List<ChatMessageResponse> getChatMessageHistory(UUID chatRoomId, UUID beforeMessageId, LocalDateTime beforeCreatedAt, int size){

        // size 검증
        if (size < 1 || size > 100) {
            throw new IllegalArgumentException("size는 1부터 100 사이의 값이어야 합니다.");
        }
        //todo: 채팅방 멤버 검증로직 추가

        List<MessageHistoryDto> dto = chatMessageMapper.findMessageHistory(chatRoomId, beforeMessageId,beforeCreatedAt,size);

        return dto.stream()
                .map(this::toChatMessageResponse)
                .toList();
    }
}
