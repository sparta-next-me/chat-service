package org.nextme.chat_server.application.exception;

import org.nextme.infrastructure.exception.ApplicationException;

public class ChatRoomException extends ApplicationException {

    public ChatRoomException(ChatRoomErrorCode errorCode) {
        super(errorCode.getHttpStatus(), errorCode.getCode(), errorCode.getDefaultMessage());
    }

    public ChatRoomException(ChatRoomErrorCode errorCode, String message) {
        super(errorCode.getHttpStatus(), errorCode.getCode(), message);
    }
}
