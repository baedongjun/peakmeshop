package com.peakmeshop.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductAttributeDTO {
    private Long id;
    private String name;
    private String code;
    private String description;
    private String type;
    private boolean required;
    private boolean filterable;
    private boolean searchable;
    private boolean comparable;
    private boolean showInProductListing;
    private List<String> options = new ArrayList<>();
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}