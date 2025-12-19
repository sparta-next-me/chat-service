package org.nextme.chat_server.infrastructure.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@FeignClient("user-service")
public interface UserServiceClient {
    @GetMapping("/feign/profile")
    UserResponse getUser(@RequestParam UUID userId);
}
