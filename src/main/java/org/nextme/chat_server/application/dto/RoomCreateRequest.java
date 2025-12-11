package org.nextme.chat_server.application.dto;

import org.nextme.chat_server.domain.chatRoom.RoomType;

import java.util.UUID;

public record RoomCreateRequest(
        RoomType roomType,
        String title,
        UUID inviteUserId,
        String invitedUserName
) {}
