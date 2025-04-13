package com.peakmeshop.domain.entity;

import com.peakmeshop.domain.entity.base.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "popups")
@Getter
@Setter
public class Popup extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
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

    private boolean newWindow;

    private boolean scrollable;

    private boolean resizable;

    private boolean draggable;

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