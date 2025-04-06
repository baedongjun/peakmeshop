package com.peakmeshop.api.controller;

import com.peakmeshop.api.dto.SettingDTO;
import com.peakmeshop.domain.service.SettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/settings")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class SettingController {

    private final SettingService settingService;

    @GetMapping("/{type}")
    public ResponseEntity<SettingDTO> getSettings(@PathVariable String type) {
        return ResponseEntity.ok(settingService.getSettings(type));
    }

    @PutMapping("/{type}")
    public ResponseEntity<SettingDTO> updateSettings(
            @PathVariable String type,
            @RequestBody SettingDTO settingDTO) {
        return ResponseEntity.ok(settingService.updateSettings(type, settingDTO));
    }

    @GetMapping("/delivery-companies")
    public ResponseEntity<SettingDTO> getDeliveryCompanies() {
        return ResponseEntity.ok(settingService.getDeliveryCompanies());
    }

    @GetMapping("/payment-methods")
    public ResponseEntity<SettingDTO> getPaymentMethods() {
        return ResponseEntity.ok(settingService.getPaymentMethods());
    }
} 