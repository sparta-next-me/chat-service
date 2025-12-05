package org.nextme.chat_server;

import org.nextme.chat_server.infrastructure.security.JwtProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(JwtProperties.class)
public class ChatServerApplication {

	public static void main(String[] args) {
        SpringApplication.run(ChatServerApplication.class, args);
	}

}
