package com.peakmeshop.domain.service;

import com.peakmeshop.api.dto.SettingDTO;

public interface SettingService {
    SettingDTO getSettings(String type);
    SettingDTO updateSettings(String type, SettingDTO settingDTO);
    SettingDTO getDeliveryCompanies();
    SettingDTO getPaymentMethods();
} 