package com.peakmeshop.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "admin_session")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminSession {
    @Id
    @Column(name = "session_id", nullable = false)
    private String sessionId;

    @ManyToOne
    @JoinColumn(name = "admin_id", nullable = false)
    private Admin admin;

    @Column(name = "ip_address", nullable = false)
    private String ipAddress;

    @Column(name = "location")
    private String location;

    @Column(name = "device_type", nullable = false)
    private String deviceType;

    @Column(name = "device_info", nullable = false)
    private String deviceInfo;

    @Column(name = "browser_info", nullable = false)
    private String browserInfo;

    @Column(name = "user_agent", nullable = false)
    private String userAgent;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "last_active_at", nullable = false)
    private LocalDateTime lastActiveAt;

    @Column(name = "expired_at")
    private LocalDateTime expiredAt;
}