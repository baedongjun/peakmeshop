package com.peakmeshop.domain.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.peakmeshop.api.dto.SettingDTO;
import com.peakmeshop.domain.entity.Setting;
import com.peakmeshop.domain.repository.SettingRepository;
import com.peakmeshop.domain.service.SettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SettingServiceImpl implements SettingService {

    private final SettingRepository settingRepository;
    private final ObjectMapper objectMapper;

    @Override
    public SettingDTO getSettings(String type) {
        Setting setting = settingRepository.findById(type)
                .orElseGet(() -> createDefaultSetting(type));
        return convertToDTO(setting);
    }

    @Override
    @Transactional
    public SettingDTO updateSettings(String type, SettingDTO settingDTO) {
        Setting setting = settingRepository.findById(type)
                .orElseGet(() -> createDefaultSetting(type));

        updateSettingFields(setting, settingDTO);
        setting = settingRepository.save(setting);
        return convertToDTO(setting);
    }

    @Override
    public SettingDTO getDeliveryCompanies() {
        SettingDTO dto = new SettingDTO();
        Map<String, Object> companies = new HashMap<>();
        companies.put("CJ대한통운", "04");
        companies.put("우체국택배", "01");
        companies.put("한진택배", "05");
        companies.put("롯데택배", "08");
        companies.put("로젠택배", "06");
        dto.setSettings(companies);
        return dto;
    }

    @Override
    public SettingDTO getPaymentMethods() {
        SettingDTO dto = new SettingDTO();
        Map<String, Object> methods = new HashMap<>();
        methods.put("신용카드", "card");
        methods.put("계좌이체", "transfer");
        methods.put("가상계좌", "vbank");
        methods.put("휴대폰결제", "phone");
        dto.setSettings(methods);
        return dto;
    }

    private Setting createDefaultSetting(String type) {
        Setting setting = new Setting();
        setting.setType(type);
        return setting;
    }

    private void updateSettingFields(Setting setting, SettingDTO dto) {
        // 기본 설정
        setting.setShopName(dto.getShopName());
        setting.setAdminEmail(dto.getAdminEmail());
        setting.setCustomerServicePhone(dto.getCustomerServicePhone());
        setting.setOperatingHours(dto.getOperatingHours());
        setting.setCompanyInfo(dto.getCompanyInfo());
        setting.setTermsOfService(dto.getTermsOfService());
        setting.setPrivacyPolicy(dto.getPrivacyPolicy());

        // 결제 설정
        setting.setCreditCardEnabled(dto.getCreditCardEnabled());
        setting.setBankTransferEnabled(dto.getBankTransferEnabled());
        setting.setVirtualAccountEnabled(dto.getVirtualAccountEnabled());
        setting.setMobilePayEnabled(dto.getMobilePayEnabled());
        setting.setMinimumOrderAmount(dto.getMinimumOrderAmount());
        setting.setPgProvider(dto.getPgProvider());
        setting.setMerchantId(dto.getMerchantId());
        setting.setMerchantKey(dto.getMerchantKey());

        // 배송 설정
        setting.setDefaultDeliveryFee(dto.getDefaultDeliveryFee());
        setting.setFreeDeliveryThreshold(dto.getFreeDeliveryThreshold());
        setting.setDefaultDeliveryCompany(dto.getDefaultDeliveryCompany());
        setting.setReturnAddress(dto.getReturnAddress());
        setting.setReturnAddressDetail(dto.getReturnAddressDetail());
        setting.setReturnZipCode(dto.getReturnZipCode());

        // 포인트 설정
        setting.setPointRate(dto.getPointRate());
        setting.setMinimumPointAmount(dto.getMinimumPointAmount());
        setting.setSignupPoint(dto.getSignupPoint());
        setting.setReviewPoint(dto.getReviewPoint());

        // SMS 설정
        setting.setSmsProvider(dto.getSmsProvider());
        setting.setSmsApiKey(dto.getSmsApiKey());
        setting.setSmsApiSecret(dto.getSmsApiSecret());
        setting.setSmsSenderId(dto.getSmsSenderId());
        setting.setOrderSmsEnabled(dto.getOrderSmsEnabled());
        setting.setDeliverySmsEnabled(dto.getDeliverySmsEnabled());

        try {
            setting.setSettings(objectMapper.writeValueAsString(dto.getSettings()));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize settings", e);
        }
    }

    private SettingDTO convertToDTO(Setting setting) {
        SettingDTO dto = new SettingDTO();
        dto.setType(setting.getType());

        // 기본 설정
        dto.setShopName(setting.getShopName());
        dto.setAdminEmail(setting.getAdminEmail());
        dto.setCustomerServicePhone(setting.getCustomerServicePhone());
        dto.setOperatingHours(setting.getOperatingHours());
        dto.setCompanyInfo(setting.getCompanyInfo());
        dto.setTermsOfService(setting.getTermsOfService());
        dto.setPrivacyPolicy(setting.getPrivacyPolicy());

        // 결제 설정
        dto.setCreditCardEnabled(setting.getCreditCardEnabled());
        dto.setBankTransferEnabled(setting.getBankTransferEnabled());
        dto.setVirtualAccountEnabled(setting.getVirtualAccountEnabled());
        dto.setMobilePayEnabled(setting.getMobilePayEnabled());
        dto.setMinimumOrderAmount(setting.getMinimumOrderAmount());
        dto.setPgProvider(setting.getPgProvider());
        dto.setMerchantId(setting.getMerchantId());
        dto.setMerchantKey(setting.getMerchantKey());

        // 배송 설정
        dto.setDefaultDeliveryFee(setting.getDefaultDeliveryFee());
        dto.setFreeDeliveryThreshold(setting.getFreeDeliveryThreshold());
        dto.setDefaultDeliveryCompany(setting.getDefaultDeliveryCompany());
        dto.setReturnAddress(setting.getReturnAddress());
        dto.setReturnAddressDetail(setting.getReturnAddressDetail());
        dto.setReturnZipCode(setting.getReturnZipCode());

        // 포인트 설정
        dto.setPointRate(setting.getPointRate());
        dto.setMinimumPointAmount(setting.getMinimumPointAmount());
        dto.setSignupPoint(setting.getSignupPoint());
        dto.setReviewPoint(setting.getReviewPoint());

        // SMS 설정
        dto.setSmsProvider(setting.getSmsProvider());
        dto.setSmsApiKey(setting.getSmsApiKey());
        dto.setSmsApiSecret(setting.getSmsApiSecret());
        dto.setSmsSenderId(setting.getSmsSenderId());
        dto.setOrderSmsEnabled(setting.getOrderSmsEnabled());
        dto.setDeliverySmsEnabled(setting.getDeliverySmsEnabled());

        try {
            if (setting.getSettings() != null) {
                dto.setSettings(objectMapper.readValue(setting.getSettings(), Map.class));
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to deserialize settings", e);
        }

        return dto;
    }
} 