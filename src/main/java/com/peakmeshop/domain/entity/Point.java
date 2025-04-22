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
import lombok.Getter;
import lombok.AccessLevel;

@Getter
@Entity
@Table(name = "points")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Point extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(mappedBy = "point")
    private Member member;

    @Column(name = "current_points", nullable = false)
    private Integer currentPoints;

    @Column(name = "total_earned_points", nullable = false)
    private Integer totalEarnedPoints;

    @Column(name = "total_used_points", nullable = false)
    private Integer totalUsedPoints;

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

    public void addPoints(Integer points) {
        this.currentPoints += points;
        this.totalEarnedPoints += points;
    }

    public void deductPoints(Integer points) {
        if (this.currentPoints < points) {
            throw new IllegalArgumentException("Not enough points");
        }
        this.currentPoints -= points;
        this.totalUsedPoints += points;
    }

    public void usePoints(Integer points) {
        deductPoints(points);
    }

    public void earnPoints(Long points) {
        addPoints(points.intValue());
    }

    public void refundPoints(Long points) {
        addPoints(points.intValue());
        this.totalUsedPoints -= points.intValue();
    }

    public void usePoints(Long points) {
        deductPoints(points.intValue());
    }
}