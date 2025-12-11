package org.nextme.chat_server.infrastructure.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * Redis pub/sub 메세지 발생
 */
@Component
@RequiredArgsConstructor
public class RedisPublisher {

    private final RedisTemplate<String, Object> redisTemplate;

    //topic(채팅방 ID)으로 메세지 발행
    public void publish(String topic, Object message) {
        redisTemplate.convertAndSend(topic, message);
    }
}
