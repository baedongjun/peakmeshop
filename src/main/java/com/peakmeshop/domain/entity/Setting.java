package com.peakmeshop.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

@Entity
@Table(name = "settings")
@Getter
@Setter
public class Setting {
    @Id
    @Column(nullable = false)
    private String type;

    @Column(columnDefinition = "json")
    private String settings;

    // 기본 설정
    private String shopName;
    private String adminEmail;
    private String customerServicePhone;
    private String operatingHours;
    @Column(columnDefinition = "TEXT")
    private String companyInfo;
    @Column(columnDefinition = "TEXT")
    private String termsOfService;
    @Column(columnDefinition = "TEXT")
    private String privacyPolicy;

    // 결제 설정
    private Boolean creditCardEnabled = true;
    private Boolean bankTransferEnabled = true;
    private Boolean virtualAccountEnabled = true;
    private Boolean mobilePayEnabled = true;
    private Integer minimumOrderAmount = 0;
    private String pgProvider;
    private String merchantId;
    private String merchantKey;

    // 배송 설정
    private Integer defaultDeliveryFee = 0;
    private Integer freeDeliveryThreshold = 0;
    private String defaultDeliveryCompany;
    private String returnAddress;
    private String returnAddressDetail;
    private String returnZipCode;

    // 포인트 설정
    private Double pointRate = 0.0;
    private Integer minimumPointAmount = 0;
    private Integer signupPoint = 0;
    private Integer reviewPoint = 0;

    // SMS 설정
    private String smsProvider;
    private String smsApiKey;
    private String smsApiSecret;
    private String smsSenderId;
    private Boolean orderSmsEnabled = false;
    private Boolean deliverySmsEnabled = false;
} 