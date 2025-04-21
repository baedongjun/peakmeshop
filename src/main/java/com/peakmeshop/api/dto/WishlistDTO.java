package com.peakmeshop.api.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.peakmeshop.domain.entity.Wishlist;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WishlistDTO {

    private Long id;
    private Long memberId;
    private String name;
    private boolean isDefault;
    private boolean isPublic;
    private String shareUrl;
    private int itemCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<WishlistItemDTO> items;

    public Wishlist toEntity() {
        return Wishlist.builder()
                .id(id)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }
}