package org.nextme.chat_server.application.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

public record ChatMessageRequest(
        String content
) {}
