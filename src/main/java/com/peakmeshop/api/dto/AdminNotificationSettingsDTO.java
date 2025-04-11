package com.peakmeshop.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminNotificationSettingsDTO {
    private Long adminId;
    private boolean emailNotifications;
    private boolean pushNotifications;
    private boolean smsNotifications;
    private boolean orderNotifications;
    private boolean stockNotifications;
    private boolean memberNotifications;
    private boolean reviewNotifications;
    private boolean securityNotifications;
}