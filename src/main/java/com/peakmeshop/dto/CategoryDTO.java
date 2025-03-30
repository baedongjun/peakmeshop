package com.peakmeshop.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDTO {
        private Long id;
        private String name;
        private String description;
        private String slug;
        private Long parentId;
        private String parentName;
        private String imageUrl;
        private boolean active;
        private boolean featured;
        private Integer sortOrder;
        private List<String> filterableAttributes;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
}