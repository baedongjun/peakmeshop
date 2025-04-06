package com.peakmeshop.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuDTO {
    private Long id;
    private String name;
    private String type;
    private String url;
    private String icon;
    private Integer order;
    private Long parentId;
    private Boolean isActive;
    private String target;
} 