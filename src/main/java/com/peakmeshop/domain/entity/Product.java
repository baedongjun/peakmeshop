package com.peakmeshop.domain.entity;

import com.peakmeshop.common.converter.StringListConverter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    // 상품 재고 상태 상수
    public static final String STOCK_STATUS_IN_STOCK = "IN_STOCK";
    public static final String STOCK_STATUS_OUT_OF_STOCK = "OUT_OF_STOCK";
    public static final String STOCK_STATUS_BACKORDER = "BACKORDER";
    public static final String STOCK_STATUS_PREORDER = "PREORDER";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private BigDecimal cost;

    @Column(name = "sale_price")
    private BigDecimal salePrice;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "brand_id")
    private Brand brand;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;

    @Column(name = "main_image")
    private String mainImage;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ProductImage> images = new ArrayList<>();

    private Integer stock;

    private Integer stockAlert;

    private Integer maxPurchaseQuantity;

    @Column(columnDefinition = "TEXT")
    private String shortDescription;

    private String status;

    private String slug;

    private Boolean isFeatured;

    private Boolean isActive;

    @Column(name = "average_rating")
    private Double averageRating;

    @Column(name = "review_count")
    private Integer reviewCount;

    @Column(name = "sales_count")
    private Integer salesCount;

    @Column(name = "view_count")
    private Integer viewCount;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ProductOption> options = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ProductVariant> variants = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Review> reviews = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "product_tags",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    @Builder.Default
    private List<Tag> tags = new ArrayList<>();

    @OneToMany(mappedBy = "product")
    private List<OrderItem> orderItems = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (cost == null) {
            cost = BigDecimal.ZERO;
        }
        if (viewCount == null) {
            viewCount = 0;
        }
        if (salesCount == null) {
            salesCount = 0;
        }
        if (reviewCount == null) {
            reviewCount = 0;
        }
        if (averageRating == null) {
            averageRating = 0.0;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // 최종 가격 계산 메서드
    public BigDecimal getFinalPrice() {
        return salePrice != null ? salePrice : price;
    }

    // 할인 금액 계산 메서드 추가
    public BigDecimal getDiscountAmount() {
        if (salePrice != null && price != null && salePrice.compareTo(price) < 0) {
            return price.subtract(salePrice);
        }
        return BigDecimal.ZERO;
    }

    // 상품 옵션 추가 메서드
    public void addOption(ProductOption option) {
        options.add(option);
        option.setProduct(this);
    }

    // 상품 옵션 제거 메서드
    public void removeOption(ProductOption option) {
        options.remove(option);
        option.setProduct(null);
    }

    // 상품 옵션 모두 제거 메서드
    public void clearOptions() {
        options.forEach(option -> option.setProduct(null));
        options.clear();
    }

    // 상품 변형 추가 메서드
    public void addVariant(ProductVariant variant) {
        variants.add(variant);
        variant.setProduct(this);
    }

    // 상품 변형 제거 메서드
    public void removeVariant(ProductVariant variant) {
        variants.remove(variant);
        variant.setProduct(null);
    }

    // 상품 변형 모두 제거 메서드
    public void clearVariants() {
        variants.forEach(variant -> variant.setProduct(null));
        variants.clear();
    }

    // 태그 추가 메서드
    public void addTag(Tag tag) {
        tags.add(tag);
        tag.getProducts().add(this);
    }

    // 태그 제거 메서드
    public void removeTag(Tag tag) {
        tags.remove(tag);
        tag.getProducts().remove(this);
    }

    // 태그 모두 제거 메서드
    public void clearTags() {
        for (Tag tag : new ArrayList<>(tags)) {
            removeTag(tag);
        }
    }

    /**
     * 상품의 재고 여부를 확인합니다.
     *
     * @return 재고가 있으면 true, 없으면 false
     */
    public boolean isInStock() {
        return this.stock != null && this.stock > 0;
    }

    public BigDecimal getTotalSales() {
        return orderItems.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTotalRevenue() {
        int totalQuantity = orderItems.stream()
                .mapToInt(OrderItem::getQuantity)
                .sum();
        return price.multiply(BigDecimal.valueOf(totalQuantity));
    }

    private BigDecimal getTotalCost() {
        return orderItems.stream()
                .map(item -> this.cost.multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}