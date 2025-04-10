package com.peakmeshop.domain.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.peakmeshop.api.dto.SettingDTO;
import com.peakmeshop.domain.entity.Setting;
import com.peakmeshop.domain.repository.SettingRepository;
import com.peakmeshop.domain.service.SettingService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SettingServiceImpl implements SettingService {

    private final SettingRepository settingRepository;
    private final ObjectMapper objectMapper;

    @Override
    public SettingDTO getSetting(String key) {
        return settingRepository.findByKey(key)
                .map(this::convertToDTO)
                .orElse(null);
    }

    @Override
    public List<SettingDTO> getSettingsByGroup(String group) {
        return settingRepository.findByGroupOrderByIdAsc(group)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Page<SettingDTO> getAllSettings(Pageable pageable) {
        return settingRepository.findAll(pageable)
                .map(this::convertToDTO);
    }

    @Override
    public List<SettingDTO> getPublicSettings() {
        return settingRepository.findByIsPublic(true)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public SettingDTO createSetting(SettingDTO settingDTO) {
        if (settingRepository.existsByKey(settingDTO.getKey())) {
            throw new IllegalArgumentException("이미 존재하는 설정 키입니다: " + settingDTO.getKey());
        }
        Setting setting = convertToEntity(settingDTO);
        return convertToDTO(settingRepository.save(setting));
    }

    @Override
    @Transactional
    public SettingDTO updateSetting(String key, SettingDTO settingDTO) {
        Setting setting = settingRepository.findByKey(key)
                .orElseThrow(() -> new IllegalArgumentException("설정을 찾을 수 없습니다: " + key));
        
        setting.setValue(settingDTO.getValue());
        setting.setDescription(settingDTO.getDescription());
        setting.setIsPublic(settingDTO.getIsPublic());
        setting.setIsRequired(settingDTO.getIsRequired());
        
        return convertToDTO(settingRepository.save(setting));
    }

    @Override
    @Transactional
    public void deleteSetting(String key) {
        Setting setting = settingRepository.findByKey(key)
                .orElseThrow(() -> new IllegalArgumentException("설정을 찾을 수 없습니다: " + key));
        settingRepository.delete(setting);
    }

    @Override
    public Map<String, Object> getSettingsByGroupAsMap(String group) {
        List<Setting> settings = settingRepository.findByGroupOrderByIdAsc(group);
        Map<String, Object> result = new HashMap<>();
        settings.forEach(setting -> result.put(setting.getKey(), setting.getValue()));
        return result;
    }

    @Override
    @Transactional
    @PostConstruct
    public void initializeDefaultSettings() {
        if (settingRepository.count() == 0) {
            createDefaultSettings();
        }
    }

    @Override
    public boolean validateSetting(String key, String value) {
        Setting setting = settingRepository.findByKey(key)
                .orElseThrow(() -> new IllegalArgumentException("설정을 찾을 수 없습니다: " + key));
        
        return validateSettingValue(setting.getType(), value);
    }

    @Override
    public SettingDTO.BasicSettings getBasicSettings() {
        Map<String, Object> settings = getSettingsByGroupAsMap("basic");
        return SettingDTO.BasicSettings.builder()
                .shopName((String) settings.get("shopName"))
                .adminEmail((String) settings.get("adminEmail"))
                .customerServicePhone((String) settings.get("customerServicePhone"))
                .customerServiceHours((String) settings.get("customerServiceHours"))
                .companyInfo((String) settings.get("companyInfo"))
                .termsOfService((String) settings.get("termsOfService"))
                .privacyPolicy((String) settings.get("privacyPolicy"))
                .build();
    }

    @Override
    public SettingDTO.PaymentSettings getPaymentSettings() {
        Map<String, Object> settings = getSettingsByGroupAsMap("payment");
        return SettingDTO.PaymentSettings.builder()
                .creditCardEnabled(Boolean.valueOf((String) settings.get("creditCardEnabled")))
                .bankTransferEnabled(Boolean.valueOf((String) settings.get("bankTransferEnabled")))
                .virtualAccountEnabled(Boolean.valueOf((String) settings.get("virtualAccountEnabled")))
                .mobilePayEnabled(Boolean.valueOf((String) settings.get("mobilePayEnabled")))
                .minOrderAmount(Integer.valueOf((String) settings.get("minOrderAmount")))
                .pgProvider((String) settings.get("pgProvider"))
                .pgMerchantId((String) settings.get("pgMerchantId"))
                .pgSecretKey((String) settings.get("pgSecretKey"))
                .build();
    }

    @Override
    public SettingDTO.DeliverySettings getDeliverySettings() {
        Map<String, Object> settings = getSettingsByGroupAsMap("delivery");
        return SettingDTO.DeliverySettings.builder()
                .defaultDeliveryFee(Integer.valueOf((String) settings.get("defaultDeliveryFee")))
                .freeDeliveryThreshold(Integer.valueOf((String) settings.get("freeDeliveryThreshold")))
                .defaultDeliveryCompany((String) settings.get("defaultDeliveryCompany"))
                .returnZipcode((String) settings.get("returnZipcode"))
                .returnAddress1((String) settings.get("returnAddress1"))
                .returnAddress2((String) settings.get("returnAddress2"))
                .build();
    }

    @Override
    public SettingDTO.PointSettings getPointSettings() {
        Map<String, Object> settings = getSettingsByGroupAsMap("point");
        return SettingDTO.PointSettings.builder()
                .pointRate(Double.valueOf((String) settings.get("pointRate")))
                .minPointAmount(Integer.valueOf((String) settings.get("minPointAmount")))
                .signupPoint(Integer.valueOf((String) settings.get("signupPoint")))
                .reviewPoint(Integer.valueOf((String) settings.get("reviewPoint")))
                .build();
    }

    @Override
    public SettingDTO.SmsSettings getSmsSettings() {
        Map<String, Object> settings = getSettingsByGroupAsMap("sms");
        return SettingDTO.SmsSettings.builder()
                .smsProvider((String) settings.get("smsProvider"))
                .smsApiKey((String) settings.get("smsApiKey"))
                .smsApiSecret((String) settings.get("smsApiSecret"))
                .smsSenderId((String) settings.get("smsSenderId"))
                .orderSmsEnabled(Boolean.valueOf((String) settings.get("orderSmsEnabled")))
                .deliverySmsEnabled(Boolean.valueOf((String) settings.get("deliverySmsEnabled")))
                .build();
    }

    @Override
    @Transactional
    public void updateBasicSettings(SettingDTO.BasicSettings settings) {
        updateSetting("shopName", createDTO("shopName", settings.getShopName(), "basic"));
        updateSetting("adminEmail", createDTO("adminEmail", settings.getAdminEmail(), "basic"));
        updateSetting("customerServicePhone", createDTO("customerServicePhone", settings.getCustomerServicePhone(), "basic"));
        updateSetting("customerServiceHours", createDTO("customerServiceHours", settings.getCustomerServiceHours(), "basic"));
        updateSetting("companyInfo", createDTO("companyInfo", settings.getCompanyInfo(), "basic"));
        updateSetting("termsOfService", createDTO("termsOfService", settings.getTermsOfService(), "basic"));
        updateSetting("privacyPolicy", createDTO("privacyPolicy", settings.getPrivacyPolicy(), "basic"));
    }

    @Override
    @Transactional
    public void updatePaymentSettings(SettingDTO.PaymentSettings settings) {
        updateSetting("creditCardEnabled", createDTO("creditCardEnabled", String.valueOf(settings.getCreditCardEnabled()), "payment"));
        updateSetting("bankTransferEnabled", createDTO("bankTransferEnabled", String.valueOf(settings.getBankTransferEnabled()), "payment"));
        updateSetting("virtualAccountEnabled", createDTO("virtualAccountEnabled", String.valueOf(settings.getVirtualAccountEnabled()), "payment"));
        updateSetting("mobilePayEnabled", createDTO("mobilePayEnabled", String.valueOf(settings.getMobilePayEnabled()), "payment"));
        updateSetting("minOrderAmount", createDTO("minOrderAmount", String.valueOf(settings.getMinOrderAmount()), "payment"));
        updateSetting("pgProvider", createDTO("pgProvider", settings.getPgProvider(), "payment"));
        updateSetting("pgMerchantId", createDTO("pgMerchantId", settings.getPgMerchantId(), "payment"));
        if (settings.getPgSecretKey() != null && !settings.getPgSecretKey().isEmpty()) {
            updateSetting("pgSecretKey", createDTO("pgSecretKey", settings.getPgSecretKey(), "payment"));
        }
    }

    @Override
    @Transactional
    public void updateDeliverySettings(SettingDTO.DeliverySettings settings) {
        updateSetting("defaultDeliveryFee", createDTO("defaultDeliveryFee", String.valueOf(settings.getDefaultDeliveryFee()), "delivery"));
        updateSetting("freeDeliveryThreshold", createDTO("freeDeliveryThreshold", String.valueOf(settings.getFreeDeliveryThreshold()), "delivery"));
        updateSetting("defaultDeliveryCompany", createDTO("defaultDeliveryCompany", settings.getDefaultDeliveryCompany(), "delivery"));
        updateSetting("returnZipcode", createDTO("returnZipcode", settings.getReturnZipcode(), "delivery"));
        updateSetting("returnAddress1", createDTO("returnAddress1", settings.getReturnAddress1(), "delivery"));
        updateSetting("returnAddress2", createDTO("returnAddress2", settings.getReturnAddress2(), "delivery"));
    }

    @Override
    @Transactional
    public void updatePointSettings(SettingDTO.PointSettings settings) {
        updateSetting("pointRate", createDTO("pointRate", String.valueOf(settings.getPointRate()), "point"));
        updateSetting("minPointAmount", createDTO("minPointAmount", String.valueOf(settings.getMinPointAmount()), "point"));
        updateSetting("signupPoint", createDTO("signupPoint", String.valueOf(settings.getSignupPoint()), "point"));
        updateSetting("reviewPoint", createDTO("reviewPoint", String.valueOf(settings.getReviewPoint()), "point"));
    }

    @Override
    @Transactional
    public void updateSmsSettings(SettingDTO.SmsSettings settings) {
        updateSetting("smsProvider", createDTO("smsProvider", settings.getSmsProvider(), "sms"));
        updateSetting("smsApiKey", createDTO("smsApiKey", settings.getSmsApiKey(), "sms"));
        updateSetting("smsApiSecret", createDTO("smsApiSecret", settings.getSmsApiSecret(), "sms"));
        updateSetting("smsSenderId", createDTO("smsSenderId", settings.getSmsSenderId(), "sms"));
        updateSetting("orderSmsEnabled", createDTO("orderSmsEnabled", String.valueOf(settings.getOrderSmsEnabled()), "sms"));
        updateSetting("deliverySmsEnabled", createDTO("deliverySmsEnabled", String.valueOf(settings.getDeliverySmsEnabled()), "sms"));
    }

    @Override
    public Map<String, Object> getSettingsAsMap(String group) {
        List<Setting> settings = settingRepository.findByGroupOrderByIdAsc(group);
        Map<String, Object> settingsMap = new HashMap<>();
        
        for (Setting setting : settings) {
            String key = setting.getKey();
            String value = setting.getValue();
            
            // Convert value based on type
            Object convertedValue = switch (setting.getType()) {
                case "boolean" -> Boolean.parseBoolean(value);
                case "number" -> {
                    try {
                        if (value.contains(".")) {
                            yield Double.parseDouble(value);
                        } else {
                            yield Integer.parseInt(value);
                        }
                    } catch (NumberFormatException e) {
                        yield value;
                    }
                }
                default -> value;
            };
            
            settingsMap.put(key, convertedValue);
        }
        
        return settingsMap;
    }

    private void createDefaultSettings() {
        // 기본 설정
        createSetting(createDefaultSetting("shopName", "Peak Me Shop", "basic", "쇼핑몰 이름", "text", true));
        createSetting(createDefaultSetting("adminEmail", "admin@peakmeshop.com", "basic", "관리자 이메일", "email", true));
        createSetting(createDefaultSetting("customerServicePhone", "1234-5678", "basic", "고객센터 전화번호", "tel", true));
        createSetting(createDefaultSetting("customerServiceHours", "평일 09:00-18:00", "basic", "고객센터 운영시간", "text", true));
        createSetting(createDefaultSetting("companyInfo", "회사 정보", "basic", "회사 정보", "textarea", true));
        createSetting(createDefaultSetting("termsOfService", "이용약관", "basic", "이용약관", "textarea", true));
        createSetting(createDefaultSetting("privacyPolicy", "개인정보처리방침", "basic", "개인정보처리방침", "textarea", true));

        // 결제 설정
        createSetting(createDefaultSetting("creditCardEnabled", "true", "payment", "신용카드 결제", "checkbox", true));
        createSetting(createDefaultSetting("bankTransferEnabled", "true", "payment", "계좌이체", "checkbox", true));
        createSetting(createDefaultSetting("virtualAccountEnabled", "true", "payment", "가상계좌", "checkbox", true));
        createSetting(createDefaultSetting("mobilePayEnabled", "true", "payment", "휴대폰 결제", "checkbox", true));
        createSetting(createDefaultSetting("minOrderAmount", "10000", "payment", "최소 주문금액", "number", true));
        createSetting(createDefaultSetting("pgProvider", "INICIS", "payment", "PG사", "select", true));
        createSetting(createDefaultSetting("pgMerchantId", "", "payment", "상점 아이디", "text", true));
        createSetting(createDefaultSetting("pgSecretKey", "", "payment", "시크릿 키", "password", true));

        // 배송 설정
        createSetting(createDefaultSetting("defaultDeliveryFee", "3000", "delivery", "기본 배송비", "number", true));
        createSetting(createDefaultSetting("freeDeliveryThreshold", "50000", "delivery", "무료배송 기준금액", "number", true));
        createSetting(createDefaultSetting("defaultDeliveryCompany", "CJ", "delivery", "기본 배송업체", "select", true));
        createSetting(createDefaultSetting("returnZipcode", "", "delivery", "반품 우편번호", "text", true));
        createSetting(createDefaultSetting("returnAddress1", "", "delivery", "반품 기본주소", "text", true));
        createSetting(createDefaultSetting("returnAddress2", "", "delivery", "반품 상세주소", "text", true));

        // 포인트 설정
        createSetting(createDefaultSetting("pointRate", "1", "point", "적립률(%)", "number", true));
        createSetting(createDefaultSetting("minPointAmount", "1000", "point", "최소 적립금액", "number", true));
        createSetting(createDefaultSetting("signupPoint", "1000", "point", "회원가입 포인트", "number", true));
        createSetting(createDefaultSetting("reviewPoint", "100", "point", "리뷰작성 포인트", "number", true));

        // SMS 설정
        createSetting(createDefaultSetting("smsProvider", "", "sms", "SMS 제공업체", "text", true));
        createSetting(createDefaultSetting("smsApiKey", "", "sms", "API 키", "text", true));
        createSetting(createDefaultSetting("smsApiSecret", "", "sms", "API Secret", "password", true));
        createSetting(createDefaultSetting("smsSenderId", "", "sms", "발신자 번호", "text", true));
        createSetting(createDefaultSetting("orderSmsEnabled", "true", "sms", "주문알림 SMS", "checkbox", true));
        createSetting(createDefaultSetting("deliverySmsEnabled", "true", "sms", "배송알림 SMS", "checkbox", true));
    }

    private SettingDTO createDefaultSetting(String key, String value, String group, String label, String type, boolean isRequired) {
        return SettingDTO.builder()
                .key(key)
                .value(value)
                .group(group)
                .label(label)
                .type(type)
                .isRequired(isRequired)
                .isPublic(true)
                .build();
    }

    private SettingDTO createDTO(String key, String value, String group) {
        return SettingDTO.builder()
                .key(key)
                .value(value)
                .group(group)
                .build();
    }

    private boolean validateSettingValue(String type, String value) {
        if (value == null) {
            return false;
        }

        switch (type) {
            case "number":
                try {
                    Double.parseDouble(value);
                    return true;
                } catch (NumberFormatException e) {
                    return false;
                }
            case "checkbox":
                return value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false");
            case "email":
                return value.matches("^[A-Za-z0-9+_.-]+@(.+)$");
            case "tel":
                return value.matches("^[0-9-]+$");
            default:
                return true;
        }
    }

    private SettingDTO convertToDTO(Setting setting) {
        return SettingDTO.builder()
                .id(setting.getId())
                .key(setting.getKey())
                .value(setting.getValue())
                .group(setting.getGroup())
                .label(setting.getLabel())
                .type(setting.getType())
                .description(setting.getDescription())
                .isRequired(setting.getIsRequired())
                .isPublic(setting.getIsPublic())
                .validationRules(setting.getValidationRules())
                .createdAt(setting.getCreatedAt())
                .updatedAt(setting.getUpdatedAt())
                .build();
    }

    private Setting convertToEntity(SettingDTO dto) {
        return Setting.builder()
                .key(dto.getKey())
                .value(dto.getValue())
                .group(dto.getGroup())
                .label(dto.getLabel())
                .type(dto.getType())
                .description(dto.getDescription())
                .isRequired(dto.getIsRequired())
                .isPublic(dto.getIsPublic())
                .validationRules(dto.getValidationRules())
                .build();
    }
} 