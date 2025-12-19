package org.nextme.chat_server.application.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ChatRoomErrorCode {

    CHAT_ROOM_ROLE_BAD(HttpStatus.BAD_REQUEST,"CHAT_ROOM_ROLE_BAD","해당 채팅방을 생성할 권한이 없습니다."),
    CHAT_ROOM_NOT_FOUND(HttpStatus.NOT_FOUND,"CHAT_ROOM_NOT_FOUND","채팅방이 없습니다."),
    CHAT_ROOM_JOIN_BAD(HttpStatus.BAD_REQUEST,"CHAT_ROOM_JOIN_BAD","채팅방 입장에 실패했습니다."),
    CHAT_ROOM_LEAVE_BAD(HttpStatus.BAD_REQUEST,"CHAT_ROOM_LEAVE_BAD","채팅방 나가기에 실패했습니다."),
    CHAT_ROOM_REQUEST_DIRECT(HttpStatus.BAD_REQUEST,"CHAT_ROOM_REQUEST_DIRECT","개인 채팅방 생성 요청이 실패했습니다."),
    CHAT_ROOM_REQUEST_AI(HttpStatus.BAD_REQUEST,"CHAT_ROOM_REQUEST_AI","AI 채팅방 생성 요청이 실패했습니다."),
    CHAT_ROOM_REQUEST_GROUP(HttpStatus.BAD_REQUEST,"CHAT_ROOM_REQUEST_GROUP","그룹 채팅방 생성 요청이 실패했습니다."),
    CHAT_ROOM_CREATE_EMPTY(HttpStatus.BAD_REQUEST,"CHAT_ROOM_CREATE_EMPTY","채팅방 생성 요청이 비었습니다."),
    CHAT_ROOM_REQUEST_EMPTY(HttpStatus.BAD_REQUEST,"CHAT_ROOM_REQUEST_EMPTY","채팅방 요청이 비었습니다."),
    CHAT_ROOM_TYPE_BAD(HttpStatus.BAD_REQUEST,"CHAT_ROOM_TYPE_BAD","잘못된 채팅방 타입입니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String defaultMessage;

    ChatRoomErrorCode(HttpStatus status, String code, String message) {
        this.httpStatus = status;
        this.code = code;
        this.defaultMessage = message;
    }
}
