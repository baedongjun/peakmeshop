package com.peakmeshop.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "settings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Setting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "setting_key", nullable = false, unique = true)
    private String key;

    @Column(name = "setting_value", columnDefinition = "TEXT")
    private String value;

    @Column(name = "setting_group", nullable = false)
    private String group;  // basic, payment, delivery, point, sms

    @Column(name = "setting_label", nullable = false)
    private String label;  // 화면에 표시될 레이블

    @Column(name = "setting_type", nullable = false)
    private String type;  // text, number, email, tel, textarea, checkbox, select

    @Column(name = "setting_description")
    private String description;

    @Column(name = "is_required")
    private Boolean isRequired;

    @Column(name = "is_public")
    private Boolean isPublic;  // 프론트엔드에 노출 여부

    @Column(name = "validation_rules")
    private String validationRules;  // min, max, pattern 등

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
} 