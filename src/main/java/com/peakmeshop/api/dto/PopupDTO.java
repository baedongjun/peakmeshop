package com.peakmeshop.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PopupDTO {
    private Long id;
    private String title;
    private String content;
    private String imageUrl;
    private String linkUrl;
    private Integer sunser;
    private Integer width;
    private Integer height;
    private Integer positionX;
    private Integer positionY;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private boolean isActive;
    private boolean isNewWindow;
    private boolean isScrollable;
    private boolean isResizable;
    private boolean isDraggable;
    private String backgroundColor;
    private String textColor;
    private String borderColor;
    private Integer borderWidth;
    private Integer borderRadius;
    private String shadowColor;
    private Integer shadowBlur;
    private Integer shadowSpread;
    private Integer shadowX;
    private Integer shadowY;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 