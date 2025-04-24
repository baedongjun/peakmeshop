package com.peakmeshop.api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberDTO {

    private Long id;

    @NotBlank(message = "아이디는 필수 입력값입니다.")
    @Size(min = 4, max = 20, message = "아이디는 4자 이상 20자 이하로 입력해주세요.")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "아이디는 영문, 숫자, 언더스코어(_)만 사용 가능합니다.")
    private String userId;

    @NotBlank(message = "이메일은 필수 입력값입니다.")
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    private String email;

    @NotBlank(message = "비밀번호는 필수 입력값입니다.")
    @Size(min = 8, message = "비밀번호는 8자 이상이어야 합니다.")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$",
            message = "비밀번호는 영문, 숫자, 특수문자를 포함해야 합니다.")
    private String password;

    @NotBlank(message = "비밀번호 확인은 필수 입력값입니다.")
    private String passwordConfirm;

    @NotBlank(message = "이름은 필수 입력값입니다.")
    @Size(min = 2, max = 50, message = "이름은 2자 이상 50자 이하로 입력해주세요.")
    private String name;

    @Pattern(regexp = "^(01[016789])(\\d{3,4})(\\d{4})$", message = "올바른 휴대폰 번호를 입력해주세요.")
    private String phone;

    @AssertTrue(message = "이용약관에 동의해주세요.")
    private boolean agreeTerms;

    private boolean agreeMarketing;
    private boolean isWithdrawn;
    private String userRole;
    private boolean enabled;
    private String status;
    private Long gradeId;
    private String gradeName;
    private Integer currentPoint;
    private boolean isEmailVerified;
    private String provider;
    private String providerId;
    private String providerType;
    private String imageUrl;
    private Integer orderCount;
    private BigDecimal totalOrderAmount;
    private LocalDate birthDate;
    private String gender;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastLoginAt;
    private List<AddressDTO> addresses;
    private List<CouponDTO> coupons;
    private Integer totalOrders;
    private Integer totalReviews;
    private Boolean isActive;
}