package org.nextme.chat_server.presentation.controller;

import io.swagger.v3.oas.annotations.Operation;
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
@RequestMapping("/v1/chats")
public class ChatRoomController {

    private final ChatRoomService chatRoomService;
    private final ChatMessageService chatMessageService;

    /**
     * 채팅방 생성
     * post
     * @param
     * @return
     */
    @Operation(summary = "채팅방 생성",
            description =
            "채팅방을 생성합니다. roomType은 enum 형식으로 `ADVICE, DIRECT, GROUP, AI` 를 받습니다. " +
            "`GROUP` 형식의 채팅방은 title 을 요구하며 그외의 채팅방은 title 이 자동 생성됩니다." +
            "`ADVICE, DIRECT` 형식의 채팅방은 생성시에 초대할 사용자 UUID와 userName을 필요로 합니다.*")
    @PostMapping("/room")
    public ResponseEntity<RoomCreateResponse> createChatRoom(@AuthenticationPrincipal UserPrincipal principal, @RequestBody RoomCreateRequest request) {
        return ResponseEntity.ok(chatRoomService.createChatRoom(principal, request));
    }

    /**
     * 채팅방 목록 조회
     * @param
     * @return
     */
    @Operation(summary = "채팅방 목록 조회",
            description = "현재 로그인 사용자가 참여하고 있는 채팅방 리스트를 제공합니다.")
    @GetMapping("/room")
    public ResponseEntity<List<RoomSearchResponse>> searchChatRooms(@AuthenticationPrincipal UserPrincipal principal,
                                                                    @RequestParam RoomType roomType) {
        return ResponseEntity.ok(chatRoomService.getChatRoomList(principal, roomType));
    }

    /**
     * 채팅방 메세지 히스토리 조회
     * @param
     * @return
     */
    @Operation(summary = "채팅방 메세지 히스토리 조회",
            description = "무한 스크롤을 위한 과거 메시지 이력을 페이징 처리하여 제공합니다.")
    @GetMapping("/message/{chatRoomId}")
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
    @Operation(summary = "채팅방 입장",
            description = "요청 사용자의 참여 권한을 확인하고, 채팅방 정보를 반환합니다.")
    @PostMapping("/room/{chatRoomId}/join")
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
    @Operation(summary = "채팅방 최장",
            description = "참여중인 채팅방을 퇴장합니다. 해당 채팅방의 모든 참여 멤버의 상태가 leave 일 경우 채팅방이 삭제처리 됩니다.")
    @PostMapping("/room/{chatRoomId}/leave")
    public ResponseEntity<Void> leaveChatRoom(@AuthenticationPrincipal UserPrincipal principal,
                                              @PathVariable UUID chatRoomId){
        chatRoomService.leaveChatRoom(principal, ChatRoomId.of(chatRoomId));
        return ResponseEntity.ok().build();
    }
}
