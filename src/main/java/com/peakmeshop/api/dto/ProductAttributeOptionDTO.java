package com.peakmeshop.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductAttributeOptionDTO {
    private Long id;
    private String name;
    private String displayName;
    private String description;
    private String type;
    private boolean required;
    private boolean filterable;
    private boolean searchable;
    private boolean comparable;
    private boolean showInProductListing;
    private Integer sortOrder;
    private boolean enabled;
    private List<String> values = new ArrayList<>();
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 