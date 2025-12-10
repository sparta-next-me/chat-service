package org.nextme.chat_server.presentation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.nextme.chat_server.application.dto.*;
import org.nextme.chat_server.application.service.ChatMessageService;
import org.nextme.chat_server.application.service.ChatRoomService;
import org.nextme.chat_server.domain.chatRoom.RoomType;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<RoomCreateResponse> createChatRoom(@RequestBody RoomCreateRequest request) {
        return ResponseEntity.ok(chatRoomService.createChatRoom(request));
    }

    /**
     * 채팅방 목록 조회
     * @param
     * @return
     */
    @GetMapping
    public ResponseEntity<List<RoomSearchResponse>> searchChatRooms(@RequestParam UUID userId,
                                                                    @RequestParam RoomType roomType) {
        return ResponseEntity.ok(chatRoomService.getChatRoomList(userId, roomType));
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

        return ResponseEntity.ok(chatMessageService.getChatMessageHistory(chatRoomId, beforeMessageId, beforeCreatedAt ,size));
    }


    /**
     * 채팅방 입장
     * @param
     * @return
     */

    /**
     * 채팅방 삭제
     * @param
     * @return
     */
}
