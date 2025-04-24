package com.peakmeshop.domain.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "tags")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(unique = true)
    private String slug;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Boolean isActive;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @ManyToMany(mappedBy = "tags")
    @Builder.Default
    private List<Product> products = new ArrayList<>();

    // 상품 추가 메서드
    public void addProduct(Product product) {
        products.add(product);
        product.getTags().add(this);
    }

    // 상품 제거 메서드
    public void removeProduct(Product product) {
        products.remove(product);
        product.getTags().remove(this);
    }

    // slug 생성 메서드 추가
    @PrePersist
    @PreUpdate
    public void createSlug() {
        if (name != null && (slug == null || slug.isEmpty())) {
            // 간단한 슬러그 생성 로직 (공백을 하이픈으로 변경하고 소문자로 변환)
            this.slug = name.toLowerCase().replaceAll("\\s+", "-")
                    .replaceAll("[^a-z0-9-]", "");
        }
    }
}