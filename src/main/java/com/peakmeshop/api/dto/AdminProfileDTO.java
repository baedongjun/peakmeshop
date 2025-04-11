package com.peakmeshop.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminProfileDTO {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String position;
    private String timezone;
    private String language;
}