package org.nextme.chat_server.presentation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.nextme.chat_server.application.dto.*;
import org.nextme.chat_server.application.service.ChatMessageService;
import org.nextme.chat_server.application.service.ChatRoomService;
import org.nextme.chat_server.domain.chatRoom.ChatRoomId;
import org.nextme.chat_server.domain.chatRoom.RoomType;
import org.nextme.common.security.UserPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/chat-rooms")
public class ChatRoomController {

    private final ChatRoomService chatRoomService;
    private final ChatMessageService chatMessageService;

    /**
     * 채팅방 생성
     * post
     * @param
     * @return
     */
    @PostMapping
    public ResponseEntity<RoomCreateResponse> createChatRoom(@AuthenticationPrincipal UserPrincipal principal, @RequestBody RoomCreateRequest request) {
        return ResponseEntity.ok(chatRoomService.createChatRoom(principal, request));
    }

    /**
     * 채팅방 목록 조회
     * @param
     * @return
     */
    @GetMapping
    public ResponseEntity<List<RoomSearchResponse>> searchChatRooms(@AuthenticationPrincipal UserPrincipal principal,
                                                                    @RequestParam RoomType roomType) {
        return ResponseEntity.ok(chatRoomService.getChatRoomList(principal, roomType));
    }

    /**
     * 채팅방 메세지 히스토리 조회
     * @param
     * @return
     */
    @GetMapping("/{chatRoomId}/messages")
    public ResponseEntity<List<ChatMessageResponse>> getMessageHistory(@PathVariable UUID chatRoomId,
                                                                       @RequestParam(required = false) UUID beforeMessageId,
                                                                       @RequestParam(required = false) LocalDateTime beforeCreatedAt,
                                                                       @RequestParam(defaultValue = "20") int size) {

        //TODO: 사용자 인증 및 값 검증 추가 @AuthenticationPrincipal 사용
        return ResponseEntity.ok(chatMessageService.getChatMessageHistory(chatRoomId, beforeMessageId, beforeCreatedAt ,size));
    }


    /**
     * 채팅방 입장
     * @param
     * @return
     */
    @PostMapping("/{chatRoomId}/join")
    public ResponseEntity<Void> inChatRoom(@AuthenticationPrincipal UserPrincipal principal,
                                           @PathVariable UUID chatRoomId){
        chatRoomService.joinChatRoom(principal, ChatRoomId.of(chatRoomId));
        return ResponseEntity.ok().build();
    }

    /**
     * 채팅방 퇴장
     * @param
     * @return
     */
    @PostMapping("/{chatRoomId}/leave")
    public ResponseEntity<Void> leaveChatRoom(@AuthenticationPrincipal UserPrincipal principal,
                                              @PathVariable UUID chatRoomId){
        chatRoomService.leaveChatRoom(principal, ChatRoomId.of(chatRoomId));
        return ResponseEntity.ok().build();
    }
}
