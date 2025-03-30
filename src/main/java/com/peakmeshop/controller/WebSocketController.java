package com.peakmeshop.controller;

import java.time.LocalDateTime;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.peakmeshop.dto.WebSocketMessage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@Slf4j
public class WebSocketController {

    private final SimpMessagingTemplate messagingTemplate;

    /**
     * 공개 채널로 메시지 전송
     */
    @MessageMapping("/message.send")
    @SendTo("/topic/public")
    public WebSocketMessage sendMessage(@Payload WebSocketMessage message) {
        return new WebSocketMessage(
                message.type(),
                message.content(),
                message.data(),
                message.sender(),
                "public",
                LocalDateTime.now()
        );
    }

    /**
     * 특정 사용자에게 메시지 전송
     */
    @MessageMapping("/message.private")
    public void sendPrivateMessage(@Payload WebSocketMessage message) {
        messagingTemplate.convertAndSendToUser(
                message.recipient(),
                "/queue/messages",
                new WebSocketMessage(
                        message.type(),
                        message.content(),
                        message.data(),
                        message.sender(),
                        message.recipient(),
                        LocalDateTime.now()
                )
        );
    }

    /**
     * 사용자 연결 시 처리
     */
    @MessageMapping("/user.connect")
    @SendTo("/topic/public")
    public WebSocketMessage connect(@Payload WebSocketMessage message, SimpMessageHeaderAccessor headerAccessor) {
        // 세션에 사용자 정보 저장
        headerAccessor.getSessionAttributes().put("username", message.sender());

        return new WebSocketMessage(
                "CONNECT",
                message.sender() + "님이 접속했습니다.",
                null,
                message.sender(),
                "public",
                LocalDateTime.now()
        );
    }
}