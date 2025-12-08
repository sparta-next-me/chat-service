package org.nextme.chat_server.application.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ChatRoomErrorCode {

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
