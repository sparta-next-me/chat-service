package org.nextme.chat_server.infrastructure.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CommonResponse<T>(
        boolean isSuccess,  // 응답 성공 여부
        String code,        // 응답 코드 값
        String message,     // 응답 메시지
        T result            // 실제 비즈니스 데이터
) {
}
