package com.peakmeshop.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "product_options")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductOption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(nullable = false)
    private String name;

    @Column(name = "display_name", nullable = false)
    private String displayName;

    private String description;

    @Column(nullable = false)
    private String type; // color, size, material 등

    @Column(nullable = false)
    private boolean required = false;

    @Column(nullable = false)
    private boolean multiple = false; // 다중 선택 가능 여부

    @Column(name = "min_selection")
    private Integer minSelection; // 최소 선택 개수

    @Column(name = "max_selection")
    private Integer maxSelection; // 최대 선택 개수

    @Column(name = "default_value")
    private String defaultValue; // 기본 선택 값

    @Column(name = "sort_order")
    private Integer sortOrder;

    @Column(columnDefinition = "boolean default true")
    private boolean enabled = true;

    @Column(name = "show_in_product_listing")
    private boolean showInProductListing = false;

    @Column(name = "show_in_cart")
    private boolean showInCart = false;

    @Column(name = "show_in_order")
    private boolean showInOrder = false;

    @Column(nullable = false)
    private boolean filterable = false;

    @Column(nullable = false)
    private boolean searchable = false;

    @Column(nullable = false)
    private boolean comparable = false;

    @OneToMany(mappedBy = "option", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductOptionValue> optionValues = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public void addValue(ProductOptionValue value) {
        optionValues.add(value);
        value.setOption(this);
    }

    public void removeValue(ProductOptionValue value) {
        optionValues.remove(value);
        value.setOption(null);
    }
}