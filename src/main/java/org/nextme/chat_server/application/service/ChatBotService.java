package org.nextme.chat_server.application.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.nextme.chat_server.application.dto.ChatMessageResponse;
import org.nextme.chat_server.domain.chatMessage.ChatMessage;
import org.nextme.chat_server.domain.chatMessage.ChatMessageRepository;
import org.nextme.chat_server.domain.chatRoom.ChatRoomId;
import org.nextme.chat_server.domain.chatRoom.RoomType;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatBotService {

    private final ChatClient chatClient;
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageRepository chatMessageRepository;
    private final MessageProducer messageProducer;


    // 세션별 대화 히스토리 (웹소켓 세션 ID 기준)
    // 서버 heap에 대화 내역 저장
    private final Map<String, List<Message>> conversationHistory = new ConcurrentHashMap<>();


    /**
     * 챗봇 메시지 처리
     */
    public void handleChatBotMessage(ChatRoomId roomId, String sessionId, String userMessage) {
        log.info("챗봇 메시지 처리 시작 - roomId: {}, sessionId: {}", roomId, sessionId);

        try {
            // 1. 대화 히스토리 가져오기
            List<Message> history = conversationHistory.computeIfAbsent(sessionId, k -> new ArrayList<>());
            history.add(new UserMessage(userMessage));

            // 2. DB 참조 필요 여부 판단
            boolean needsDb = checkIfNeedsDatabase(userMessage);
            log.info("DB 참조 필요 여부: {}", needsDb);

            if (needsDb) {
                // 미래설계 서비스 비동기 요청
                generateResponseWithDb(history, roomId, sessionId);

            } else {
                // 3. AI 응답 생성
                String aiResponse = generateNormalResponse(history);

                // 4. AI 응답 히스토리에 추가
                history.add(new AssistantMessage(aiResponse));

                // 5. AI 메시지 DB 저장
                ChatMessage botMessage = ChatMessage.create(
                        roomId,
                        UUID.fromString("00000000-0000-0000-0000-000000000000"), // 챗봇 전용 고정 UUID
                        "AI",
                        aiResponse
                );
                ChatMessage savedMessage = chatMessageRepository.save(botMessage);

                // 6. 브로드캐스트
                ChatMessageResponse response = ChatMessageResponse.from(savedMessage);
                messagingTemplate.convertAndSend(
                        "/topic/chat.room." + roomId.getChatRoomId(),
                        response
                );

                log.info("챗봇 응답 전송 완료 - messageId: {}", savedMessage.getId());

            }

        } catch (Exception e) {
            log.error("챗봇 메시지 처리 실패", e);
            throw new RuntimeException("챗봇 응답 생성 실패: " + e.getMessage());
        }
    }

    private boolean checkIfNeedsDatabase(String userMessage) {
        String prompt = String.format("""
            다음 질문이 우리 서비스의 데이터베이스 조회가 필요한지 판단해줘.
            예: "미래 설계 서비스", "금융 상품 추천", "상담사 예약 내역 확인" 등은 true
            일반 대화는 false
            
            질문: %s
            
            "true" 또는 "false"로만 답변해.
            """, userMessage);

        String response = chatClient.prompt()
                .user(prompt)
                .call()
                .content();

        return response.trim().equalsIgnoreCase("true");
    }

    private String generateNormalResponse(List<Message> history) {
        return chatClient.prompt()
                .messages(history)
                .call()
                .content();
    }

    /**
     * RAG 사용해 받아올 응답은 미래설계 서비스쪽에 요청
     * @param
     * @return
     */
    private void generateResponseWithDb(List<Message> history,
                                          ChatRoomId roomId,
                                          String sessionId) throws JsonProcessingException {

        ObjectMapper om = new ObjectMapper();
        String messageHistory = om.writeValueAsString(history);

        // 카프카 메세지 전송
        messageProducer.send(roomId.getChatRoomId(),"AI", messageHistory, sessionId);
    }

    /**
     * 리스너에서 전송받은 응답 브로드 캐스팅 및 세션에 추가
     * @param
     * @return
     */
    public void listenChatbotMessage(ChatRoomId roomId, RoomType roomType, String content, String sessionId) {
        // 대화 히스토리 세션에 저장
        //todo: nullpointexception 처리
        List<Message> history = conversationHistory.computeIfAbsent(sessionId, k -> new ArrayList<>());
        history.add(new AssistantMessage(content));

        // AI 메시지 DB 저장
        ChatMessage botMessage = ChatMessage.create(
                roomId,
                UUID.fromString("00000000-0000-0000-0000-000000000000"), // 챗봇 전용 고정 UUID
                "AI",
                content
        );
        ChatMessage savedMessage = chatMessageRepository.save(botMessage);

        try{
            // 브로드캐스트
            ChatMessageResponse response = ChatMessageResponse.from(savedMessage);
            messagingTemplate.convertAndSend(
                    "/topic/chat.room." + roomId.getChatRoomId(),
                    response
            );

            log.info("챗봇 응답 전송 완료 - messageId: {}", savedMessage.getId());
        }catch (Exception e){
            log.error("챗봇 메시지 처리 실패", e);
            throw new RuntimeException("챗봇 응답 생성 실패: " + e.getMessage());
        }
    }

    /**
     * 세션 히스토리 삭제
     * @param
     * @return
     */
    public void clearHistory(String sessionId) {
        conversationHistory.remove(sessionId);
        log.info("세션 히스토리 삭제: {}", sessionId);
        log.info("챗봇 대화방 삭제");
    }
}
