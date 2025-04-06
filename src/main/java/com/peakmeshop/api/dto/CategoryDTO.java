package com.peakmeshop.api.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
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
        private Long productCount;
        private Integer depth;

        public boolean isActive() {
            return active;
        }
}