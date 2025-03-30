package com.peakmeshop.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "product_bundles")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductBundle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "discount_rate")
    private BigDecimal discountRate;

    @Column(name = "discount_amount")
    private BigDecimal discountAmount;

    @Column(nullable = false)
    private boolean active;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "bundle", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductBundleItem> items = new ArrayList<>();

    // 상품 묶음 아이템 추가 메서드
    public void addItem(ProductBundleItem item) {
        items.add(item);
        item.setBundle(this);
    }

    // 상품 묶음 아이템 제거 메서드
    public void removeItem(ProductBundleItem item) {
        items.remove(item);
        item.setBundle(null);
    }

    // 상품 묶음 아이템 모두 제거 메서드
    public void clearItems() {
        items.forEach(item -> item.setBundle(null));
        items.clear();
    }
}