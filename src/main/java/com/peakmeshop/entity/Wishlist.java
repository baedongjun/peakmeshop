package com.peakmeshop.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "wishlists")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Wishlist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    private String name;

    private boolean isDefault;

    private boolean isPublic;

    private String shareUrl;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "wishlist", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WishlistItem> items = new ArrayList<>();

    // 유틸리티 메서드
    public void addItem(WishlistItem item) {
        this.items.add(item);
        item.setWishlist(this);
    }

    public void removeItem(WishlistItem item) {
        this.items.remove(item);
        item.setWishlist(null);
    }

    public void removeItemByProductId(Long productId) {
        items.removeIf(item -> {
            if (item.getProduct().getId().equals(productId)) {
                item.setWishlist(null);
                return true;
            }
            return false;
        });
    }

    public boolean containsProduct(Long productId) {
        return items.stream()
                .anyMatch(item -> item.getProduct().getId().equals(productId));
    }
}