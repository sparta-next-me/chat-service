/**
 * @package     org.nextme.chat_server.presentation.controller
 * @class       StompChatController
 * @description 채팅 메세지를 받아 처리하는 진입점
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
package org.nextme.chat_server.presentation.controller;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.nextme.chat_server.application.dto.ChatMessageRequest;
import org.nextme.chat_server.application.dto.ChatMessageResponse;
import org.nextme.chat_server.application.service.ChatMessageService;
import org.nextme.chat_server.domain.chatRoom.ChatRoomId;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.Map;
import java.util.UUID;

@Slf4j
@Controller
@RequiredArgsConstructor
public class StompChatController {

    private final ChatMessageService chatMessageService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    /**
     * 클라이언트 -> 서버: /app/chat.send/{roomId}
     * 서버 -> 클라이언트: /topic/chat.room.{roomId}
     */
    @MessageMapping("/chat.send/{roomId}")
    public void sendMessage(
            @DestinationVariable String roomId,
            @Payload ChatMessageRequest request,
            SimpMessageHeaderAccessor headerAccessor // STOMP 헤더 + 세션에 접근하는 유틸 객체
            )
    {
        try {
            log.info("메세지 수신 -  roomId = {}, request = {}", roomId, request.content());

            // 세션에서 userId, userName 추출
            UUID senderId = (UUID) headerAccessor.getSessionAttributes().get("userId");
            String senderName = (String) headerAccessor.getSessionAttributes().get("userName");

            if (senderId == null) {
                throw new RuntimeException("인증되지 않은 사용자입니다.");
            }

            ChatRoomId chatRoomId = ChatRoomId.of(UUID.fromString(roomId));

            // 메세지 전송
            ChatMessageResponse response = chatMessageService.sendMessage(chatRoomId, senderId, senderName, request);

            // 해당 방 구독자들에게 브로드 캐스트
            // ToDo: chatMessageService에 redis Pub/Sub 해서 구현
            simpMessagingTemplate.convertAndSend(
                    "/topic/chat.room." + chatRoomId.getChatRoomId(),
                    response
            );

            log.info("메세지 브로드캐스트 완료 - {}", response.messageId());

        }catch (Exception e){
            log.info("메세지 전송 실패 - {}", roomId, e);

            // 발신자에게 에러 전송
            simpMessagingTemplate.convertAndSendToUser(
                    headerAccessor.getSessionAttributes().get("userId").toString(),
                    "/queue/errors",
                    Map.of("error", e.getMessage())
            );
        }
    }
}
