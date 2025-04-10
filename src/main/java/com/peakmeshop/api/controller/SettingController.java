package com.peakmeshop.api.controller;

import com.peakmeshop.api.dto.SettingDTO;
import com.peakmeshop.domain.entity.Setting;
import com.peakmeshop.domain.service.SettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/settings")
@RequiredArgsConstructor
public class SettingController {

    private final SettingService settingService;

    @GetMapping("/{key}")
    public ResponseEntity<SettingDTO> getSetting(@PathVariable String key) {
        return ResponseEntity.ok(settingService.getSetting(key));
    }

    @GetMapping("/group/{group}")
    public ResponseEntity<List<SettingDTO>> getSettingsByGroup(@PathVariable String group) {
        return ResponseEntity.ok(settingService.getSettingsByGroup(group));
    }

    @GetMapping("/map/{group}")
    public ResponseEntity<Map<String, Object>> getSettingsAsMap(@PathVariable String group) {
        return ResponseEntity.ok(settingService.getSettingsAsMap(group));
    }

    @GetMapping
    public ResponseEntity<Page<SettingDTO>> getAllSettings(Pageable pageable) {
        return ResponseEntity.ok(settingService.getAllSettings(pageable));
    }

    @PostMapping
    public ResponseEntity<SettingDTO> createSetting(@RequestBody SettingDTO settingDTO) {
        return ResponseEntity.ok(settingService.createSetting(settingDTO));
    }

    @PutMapping("/{key}")
    public ResponseEntity<SettingDTO> updateSetting(
            @PathVariable String key,
            @RequestBody SettingDTO settingDTO) {
        return ResponseEntity.ok(settingService.updateSetting(key, settingDTO));
    }

    @DeleteMapping("/{key}")
    public ResponseEntity<Void> deleteSetting(@PathVariable String key) {
        settingService.deleteSetting(key);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/initialize")
    public ResponseEntity<Void> initializeDefaultSettings() {
        settingService.initializeDefaultSettings();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/public")
    public ResponseEntity<List<SettingDTO>> getPublicSettings() {
        return ResponseEntity.ok(settingService.getPublicSettings());
    }

    @GetMapping("/group/{group}/map")
    public ResponseEntity<Map<String, Object>> getSettingsByGroupAsMap(@PathVariable String group) {
        return ResponseEntity.ok(settingService.getSettingsByGroupAsMap(group));
    }

    @GetMapping("/validate")
    public ResponseEntity<Boolean> validateSetting(@RequestParam String key, @RequestParam String value) {
        return ResponseEntity.ok(settingService.validateSetting(key, value));
    }

    // 그룹별 설정 API
    @GetMapping("/basic")
    public ResponseEntity<SettingDTO.BasicSettings> getBasicSettings() {
        return ResponseEntity.ok(settingService.getBasicSettings());
    }

    @GetMapping("/payment")
    public ResponseEntity<SettingDTO.PaymentSettings> getPaymentSettings() {
        return ResponseEntity.ok(settingService.getPaymentSettings());
    }

    @GetMapping("/delivery")
    public ResponseEntity<SettingDTO.DeliverySettings> getDeliverySettings() {
        return ResponseEntity.ok(settingService.getDeliverySettings());
    }

    @GetMapping("/point")
    public ResponseEntity<SettingDTO.PointSettings> getPointSettings() {
        return ResponseEntity.ok(settingService.getPointSettings());
    }

    @GetMapping("/sms")
    public ResponseEntity<SettingDTO.SmsSettings> getSmsSettings() {
        return ResponseEntity.ok(settingService.getSmsSettings());
    }

    @PutMapping("/basic")
    public ResponseEntity<Void> updateBasicSettings(@RequestBody SettingDTO.BasicSettings settings) {
        settingService.updateBasicSettings(settings);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/payment")
    public ResponseEntity<Void> updatePaymentSettings(@RequestBody SettingDTO.PaymentSettings settings) {
        settingService.updatePaymentSettings(settings);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/delivery")
    public ResponseEntity<Void> updateDeliverySettings(@RequestBody SettingDTO.DeliverySettings settings) {
        settingService.updateDeliverySettings(settings);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/point")
    public ResponseEntity<Void> updatePointSettings(@RequestBody SettingDTO.PointSettings settings) {
        settingService.updatePointSettings(settings);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/sms")
    public ResponseEntity<Void> updateSmsSettings(@RequestBody SettingDTO.SmsSettings settings) {
        settingService.updateSmsSettings(settings);
        return ResponseEntity.ok().build();
    }
} 