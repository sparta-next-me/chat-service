package org.nextme.chat_server.infrastructure.kafka.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.ToString;

import java.util.UUID;

@Getter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChatRoomCancel implements MessageTpl {
    UUID reservationId;
}
