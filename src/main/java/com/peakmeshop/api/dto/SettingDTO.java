package com.peakmeshop.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SettingDTO {
    private String type;
    private Map<String, Object> settings;
    
    // 기본 설정
    private String shopName;
    private String adminEmail;
    private String customerServicePhone;
    private String operatingHours;
    private String companyInfo;
    private String termsOfService;
    private String privacyPolicy;
    
    // 결제 설정
    private Boolean creditCardEnabled;
    private Boolean bankTransferEnabled;
    private Boolean virtualAccountEnabled;
    private Boolean mobilePayEnabled;
    private Integer minimumOrderAmount;
    private String pgProvider;
    private String merchantId;
    private String merchantKey;
    
    // 배송 설정
    private Integer defaultDeliveryFee;
    private Integer freeDeliveryThreshold;
    private String defaultDeliveryCompany;
    private String returnAddress;
    private String returnAddressDetail;
    private String returnZipCode;
    
    // 포인트 설정
    private Double pointRate;
    private Integer minimumPointAmount;
    private Integer signupPoint;
    private Integer reviewPoint;
    
    // SMS 설정
    private String smsProvider;
    private String smsApiKey;
    private String smsApiSecret;
    private String smsSenderId;
    private Boolean orderSmsEnabled;
    private Boolean deliverySmsEnabled;
} 