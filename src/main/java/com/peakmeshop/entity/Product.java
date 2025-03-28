package com.peakmeshop.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private BigDecimal price;

    private BigDecimal discountPrice;

    @Column(nullable = false)
    private Integer stockQuantity;

    private String mainImageUrl;

    @ElementCollection
    private List<String> additionalImageUrls = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(nullable = false)
    private Boolean isAvailable;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String brand;

    private Double averageRating;

    private Integer reviewCount;

    // Java 17 Text Block을 사용한 쿼리 예시 (JPA 네이티브 쿼리에 유용)
    public static final String FIND_TRENDING_PRODUCTS_QUERY = """
            SELECT p.* FROM products p
            JOIN (
                SELECT product_id, COUNT(*) as order_count
                FROM order_items
                WHERE created_at > ?1
                GROUP BY product_id
                ORDER BY order_count DESC
                LIMIT ?2
            ) o ON p.id = o.product_id
            """;
}