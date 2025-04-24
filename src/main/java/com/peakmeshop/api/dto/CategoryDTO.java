package com.peakmeshop.api.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.peakmeshop.domain.entity.Product;
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
        private List<Product> products;
        private String parentName;
        private String imageUrl;
        private boolean isActive;
        private boolean isFeatured;
        private Integer sortOrder;
        private List<String> filterableAttributes;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private Integer productCount;
        private Integer childCount;
        private Integer depth;

}