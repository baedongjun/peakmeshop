package com.peakmeshop.api.dto;

public record MenuDTO(
    Long id,
    String name,
    String url,
    String type,
    Long parentId,
    Integer sortOrder,
    boolean isActive
) {} 