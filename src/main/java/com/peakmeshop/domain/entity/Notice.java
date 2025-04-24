package com.peakmeshop.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "notices")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notice {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String title;
    
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;
    
    @Column(nullable = false)
    private String category;
    
    @Column(nullable = false)
    private Boolean important;
    
    @Column(nullable = false)
    private String status;
    
    @Column(nullable = false)
    private Integer viewCount;
    
    @Column(nullable = false)
    private boolean isTop;
    
    @Column(nullable = false)
    private LocalDateTime startAt;
    
    @Column(nullable = false)
    private LocalDateTime endAt;
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public void update(String title, String content, String category, Boolean important) {
        this.title = title;
        this.content = content;
        this.category = category;
        this.important = important;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setImportant(Boolean important) {
        this.important = important;
    }

    public void incrementViewCount() {
        this.viewCount++;
    }
} 