package org.nextme.chat_server.infrastructure.client;

import feign.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {
    @Bean
    Logger.Level feignLoggerLevel() {
        // FULL로 설정해야 요청 주소, 헤더, 그리고 우리가 궁금한 'HTML 내용(Body)'이 다 찍힙니다.
        return Logger.Level.FULL;
    }
}
