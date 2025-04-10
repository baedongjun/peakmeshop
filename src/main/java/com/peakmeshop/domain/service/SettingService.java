package com.peakmeshop.domain.service;

import com.peakmeshop.api.dto.SettingDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface SettingService {
    SettingDTO getSetting(String key);
    List<SettingDTO> getSettingsByGroup(String group);
    Page<SettingDTO> getAllSettings(Pageable pageable);
    List<SettingDTO> getPublicSettings();
    SettingDTO createSetting(SettingDTO settingDTO);
    SettingDTO updateSetting(String key, SettingDTO settingDTO);
    void deleteSetting(String key);
    Map<String, Object> getSettingsByGroupAsMap(String group);
    void initializeDefaultSettings();
    boolean validateSetting(String key, String value);
    
    // 그룹별 설정 메서드
    SettingDTO.BasicSettings getBasicSettings();
    SettingDTO.PaymentSettings getPaymentSettings();
    SettingDTO.DeliverySettings getDeliverySettings();
    SettingDTO.PointSettings getPointSettings();
    SettingDTO.SmsSettings getSmsSettings();
    
    void updateBasicSettings(SettingDTO.BasicSettings settings);
    void updatePaymentSettings(SettingDTO.PaymentSettings settings);
    void updateDeliverySettings(SettingDTO.DeliverySettings settings);
    void updatePointSettings(SettingDTO.PointSettings settings);
    void updateSmsSettings(SettingDTO.SmsSettings settings);

    Map<String, Object> getSettingsAsMap(String group);
} 