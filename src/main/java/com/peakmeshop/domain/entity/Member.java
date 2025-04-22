package com.peakmeshop.domain.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "members")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Member {

    public static final String STATUS_ACTIVE = "ACTIVE";
    public static final String STATUS_INACTIVE = "INACTIVE";
    public static final String STATUS_BLOCKED = "BLOCKED";
    public static final String STATUS_DORMANT = "DORMANT";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "아이디는 필수 입력값입니다.")
    @Size(min = 4, max = 20, message = "아이디는 4자 이상 20자 이하로 입력해주세요.")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "아이디는 영문, 숫자, 언더스코어(_)만 사용 가능합니다.")
    @Column(nullable = false, unique = true, length = 20)
    private String userId;

    @NotBlank(message = "이메일은 필수 입력값입니다.")
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @NotBlank(message = "비밀번호는 필수 입력값입니다.")
    @Column(nullable = false, length = 100)
    private String password;

    @NotBlank(message = "이름은 필수 입력값입니다.")
    @Size(min = 2, max = 50, message = "이름은 2자 이상 50자 이하로 입력해주세요.")
    @Column(nullable = false, length = 50)
    private String name;

    @Pattern(regexp = "^(01[016789])(\\d{3,4})(\\d{4})$", message = "올바른 휴대폰 번호를 입력해주세요.")
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

    @Column(name = "agree_terms")
    private boolean agreeTerms;

    @Column(name = "agree_marketing")
    private boolean agreeMarketing;

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

    @Column(nullable = false)
    private boolean isWithdrawn = false;

    @Column
    private LocalDateTime lastOrderDate;

    @Column
    private String originalStatus;

    @Column(name = "withdrawn_at")
    private LocalDateTime withdrawnAt;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "member")
    private List<MemberCoupon> coupons = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<Order> orders = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<Cart> cart = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<Wishlist> wishlist = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<Review> reviews = new ArrayList<>();

    @OneToOne
    @JoinColumn(name = "point_id")
    private Point point;

    @OneToOne
    @JoinColumn(name = "member_grade_id")
    private MemberGrade memberGrade;

    @Column
    private int orderCount;

    @Column
    private double totalOrders;

    @Column
    private double totalOrderAmount;

    @Column
    private double totalPoints;

    public boolean isActive() {
        return STATUS_ACTIVE.equals(status);
    }

    public void updateLastLogin() {
        this.lastLoginAt = LocalDateTime.now();
    }

    public void withdraw() {
        this.isWithdrawn = true;
        this.withdrawnAt = LocalDateTime.now();
    }

    public void activate() {
        this.status = STATUS_ACTIVE;
    }

    public void deactivate() {
        this.status = STATUS_INACTIVE;
    }

    public void updateOrderCount(int count) {
        this.orderCount = count;
        this.lastOrderDate = LocalDateTime.now();
    }

    public void updateTotalOrders(double orders) {
        this.totalOrders = orders;
    }

    public void updateTotalOrderAmount(double amount) {
        this.totalOrderAmount = amount;
    }

    public void updateTotalPoints(double points) {
        this.totalPoints = points;
    }
}