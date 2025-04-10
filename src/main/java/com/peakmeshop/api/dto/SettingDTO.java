package com.peakmeshop.api.dto;

import lombok.*;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SettingDTO {
    private Long id;
    
    @NotBlank(message = "설정 키는 필수입니다")
    private String key;
    
    private String value;
    private String group;
    private String label;
    private String type;
    private String description;
    private Boolean isRequired;
    private Boolean isPublic;
    private String validationRules;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 모든 설정을 포함하는 클래스
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AllSettings {
        private BasicSettings basic;
        private PaymentSettings payment;
        private DeliverySettings delivery;
        private PointSettings point;
        private SmsSettings sms;
    }

    // 그룹별 설정을 위한 중첩 클래스들
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BasicSettings {
        private String shopName;
        private String adminEmail;
        private String customerServicePhone;
        private String customerServiceHours;
        private String companyInfo;
        private String termsOfService;
        private String privacyPolicy;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentSettings {
        private Boolean creditCardEnabled;
        private Boolean bankTransferEnabled;
        private Boolean virtualAccountEnabled;
        private Boolean mobilePayEnabled;
        private Integer minOrderAmount;
        private String pgProvider;
        private String pgMerchantId;
        private String pgSecretKey;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DeliverySettings {
        private Integer defaultDeliveryFee;
        private Integer freeDeliveryThreshold;
        private String defaultDeliveryCompany;
        private String returnZipcode;
        private String returnAddress1;
        private String returnAddress2;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PointSettings {
        private Double pointRate;
        private Integer minPointAmount;
        private Integer signupPoint;
        private Integer reviewPoint;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SmsSettings {
        private String smsProvider;
        private String smsApiKey;
        private String smsApiSecret;
        private String smsSenderId;
        private Boolean orderSmsEnabled;
        private Boolean deliverySmsEnabled;
        private Boolean pointSmsEnabled;
        private Boolean marketingSmsEnabled;
    }

    // 그룹별 설정값을 Map으로 변환
    public Map<String, Object> toMap() {
        return Map.of(
            "basic", BasicSettings.builder()
                .shopName(getValue("shopName"))
                .adminEmail(getValue("adminEmail"))
                .customerServicePhone(getValue("customerServicePhone"))
                .customerServiceHours(getValue("customerServiceHours"))
                .companyInfo(getValue("companyInfo"))
                .termsOfService(getValue("termsOfService"))
                .privacyPolicy(getValue("privacyPolicy"))
                .build(),
            "payment", PaymentSettings.builder()
                .creditCardEnabled(getBooleanValue("creditCardEnabled"))
                .bankTransferEnabled(getBooleanValue("bankTransferEnabled"))
                .virtualAccountEnabled(getBooleanValue("virtualAccountEnabled"))
                .mobilePayEnabled(getBooleanValue("mobilePayEnabled"))
                .minOrderAmount(getIntegerValue("minOrderAmount"))
                .pgProvider(getValue("pgProvider"))
                .pgMerchantId(getValue("pgMerchantId"))
                .pgSecretKey(getValue("pgSecretKey"))
                .build(),
            "delivery", DeliverySettings.builder()
                .defaultDeliveryFee(getIntegerValue("defaultDeliveryFee"))
                .freeDeliveryThreshold(getIntegerValue("freeDeliveryThreshold"))
                .defaultDeliveryCompany(getValue("defaultDeliveryCompany"))
                .returnZipcode(getValue("returnZipcode"))
                .returnAddress1(getValue("returnAddress1"))
                .returnAddress2(getValue("returnAddress2"))
                .build(),
            "point", PointSettings.builder()
                .pointRate(getDoubleValue("pointRate"))
                .minPointAmount(getIntegerValue("minPointAmount"))
                .signupPoint(getIntegerValue("signupPoint"))
                .reviewPoint(getIntegerValue("reviewPoint"))
                .build(),
            "sms", SmsSettings.builder()
                .smsProvider(getValue("smsProvider"))
                .smsApiKey(getValue("smsApiKey"))
                .smsApiSecret(getValue("smsApiSecret"))
                .smsSenderId(getValue("smsSenderId"))
                .orderSmsEnabled(getBooleanValue("orderSmsEnabled"))
                .deliverySmsEnabled(getBooleanValue("deliverySmsEnabled"))
                .build()
        );
    }

    private String getValue(String key) {
        return this.key.equals(key) ? this.value : null;
    }

    private Boolean getBooleanValue(String key) {
        String value = getValue(key);
        return value != null ? Boolean.valueOf(value) : null;
    }

    private Integer getIntegerValue(String key) {
        String value = getValue(key);
        return value != null ? Integer.valueOf(value) : null;
    }

    private Double getDoubleValue(String key) {
        String value = getValue(key);
        return value != null ? Double.valueOf(value) : null;
    }
} 