package org.nextme.chat_server.infrastructure.client;

public record UserResponse(
        String name,
        String role,
        String SlackId
) {
}
