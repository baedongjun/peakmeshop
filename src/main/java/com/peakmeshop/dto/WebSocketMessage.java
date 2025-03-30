package com.peakmeshop.dto;

import java.time.LocalDateTime;

public record WebSocketMessage(
        String type,
        String content,
        Object data,
        String sender,
        String recipient,
        LocalDateTime timestamp
) {}