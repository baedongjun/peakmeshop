package com.peakmeshop.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "popups")
@Getter
@Setter
public class Popup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    private String imageUrl;

    private String linkUrl;

    private String target;

    private Integer width;

    private Integer height;

    private String position;

    @Column(name = "popup_order")
    private Integer order;

    @Column(nullable = false)
    private LocalDateTime startDateTime;

    @Column(nullable = false)
    private LocalDateTime endDateTime;

    @Column(nullable = false)
    private Boolean isActive = true;

    @Column(nullable = false)
    private String deviceType;

    private Boolean showTodayClose = true;
} 