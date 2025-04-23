package com.peakmeshop.domain.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "points")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Point {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(mappedBy = "point")
    private Member member;

    @Column(name = "current_point", nullable = false)
    private Integer currentPoint;

    @Column(name = "total_earned_point", nullable = false)
    private Integer totalEarnedPoint;

    @Column(name = "total_used_point", nullable = false)
    private Integer totalUsedPoint;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private Long amount;

    @Column(length = 100)
    private String reason;

    @Column(length = 20, nullable = false)
    private String type;  // EARN, USE, REFUND, EXPIRED

    @Column(name = "expiry_at")
    private LocalDateTime expiryAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}