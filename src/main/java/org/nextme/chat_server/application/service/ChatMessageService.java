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
import org.nextme.chat_server.domain.chatMessage.ChatMessageRepository;
import org.nextme.chat_server.domain.chatRoom.ChatRoomId;
import org.nextme.chat_server.domain.chatRoom.ChatRoomRepository;
import org.nextme.chat_server.domain.chatRoomMember.ChatRoomMemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;

    public ChatMessageResponse sendMessage(ChatRoomId roomId, UUID senderId, ChatMessageRequest request) {
        log.info("메세지 전송 - roomId = {}, senderId = {}, request = {}", roomId, senderId, request);

        //TODO: 사용자 값 검증 캐싱해서 확인

        //메세지 생성
        ChatMessage message = ChatMessage.create(
                roomId,
                senderId,
                request.content()
        );

        //메세지 저장
        ChatMessage saveMsg = chatMessageRepository.save(message);

        //TODO: 방 최근 메세지 redis 업데이트

        return ChatMessageResponse.from(saveMsg);
    }
}
