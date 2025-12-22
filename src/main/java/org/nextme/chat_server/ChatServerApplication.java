package org.nextme.chat_server;

import org.nextme.chat_server.infrastructure.security.JwtProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;


@EnableJpaAuditing
@EnableFeignClients
@EnableScheduling
@EnableConfigurationProperties(JwtProperties.class)
@SpringBootApplication(scanBasePackages = "org.nextme")
public class ChatServerApplication {

	public static void main(String[] args) {
        SpringApplication.run(ChatServerApplication.class, args);
	}

}
