package org.nextme.chat_server.application.service;

import java.util.UUID;

public interface MessageProducer {
    void send(UUID userId);
}
