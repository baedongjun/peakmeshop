package com.peakmeshop.admin.controller;

import com.peakmeshop.api.dto.SettingDTO;
import com.peakmeshop.domain.service.SettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 관리자 설정 관리 관련 뷰 컨트롤러
 */
@Controller
@RequestMapping("/admin/settings")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminSettingViewController {

    private final SettingService settingService;

    /**
     * 설정 관리 페이지
     */
    @GetMapping
    public String settingsPage(Model model) {
        // 각 설정 그룹별로 데이터를 가져와서 모델에 추가
        SettingDTO.BasicSettings basicSettings = settingService.getBasicSettings();
        SettingDTO.PaymentSettings paymentSettings = settingService.getPaymentSettings();
        SettingDTO.DeliverySettings deliverySettings = settingService.getDeliverySettings();
        SettingDTO.PointSettings pointSettings = settingService.getPointSettings();
        SettingDTO.SmsSettings smsSettings = settingService.getSmsSettings();

        // 모든 설정을 하나의 객체로 합침
        model.addAttribute("settings", new SettingDTO.AllSettings(
            basicSettings,
            paymentSettings,
            deliverySettings,
            pointSettings,
            smsSettings
        ));

        return "admin/settings/settings";
    }
} 