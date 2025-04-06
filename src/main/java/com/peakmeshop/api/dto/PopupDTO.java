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
    private String target;
    private Integer width;
    private Integer height;
    private String position;
    private Integer order;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private Boolean isActive;
    private String deviceType;
    private Boolean showTodayClose;
} 