package com.peakmeshop.domain.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "members")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Member {

    public static final String STATUS_ACTIVE = "ACTIVE";
    public static final String STATUS_INACTIVE = "INACTIVE";
    public static final String STATUS_BLOCKED = "BLOCKED";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", length = 50)
    private String userId;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 100)
    private String password;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(length = 20)
    private String phone;

    @Column(name = "user_role", nullable = false, length = 20)
    private String userRole;

    @Column(nullable = false)
    private boolean enabled;

    @Column(length = 20)
    private String status;

    @Column(name = "email_verified")
    private boolean emailVerified;

    @Column(length = 20)
    private String provider;

    @Column(name = "provider_id", length = 100)
    private String providerId;

    @Column(name = "provider_type", length = 20)
    private String providerType;

    @Column(name = "image_url", length = 255)
    private String imageUrl;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(length = 10)
    private String gender;

    @Column(name = "verification_token", length = 100)
    private String verificationToken;

    @Column(name = "reset_token", length = 100)
    private String resetToken;

    @Column(name = "reset_token_expiry")
    private LocalDateTime resetTokenExpiry;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "member")
    private List<MemberCoupon> coupons = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "member_tier_id")
    private MemberTier memberTier;

    // getter와 setter 추가
    public MemberTier getMemberTier() {
        return memberTier;
    }

    public void setMemberTier(MemberTier memberTier) {
        this.memberTier = memberTier;
    }

    public boolean isActive() {
        return STATUS_ACTIVE.equals(status);
    }
}