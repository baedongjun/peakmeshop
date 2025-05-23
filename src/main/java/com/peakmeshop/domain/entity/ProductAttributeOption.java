package com.peakmeshop.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "product_attribute_options")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductAttributeOption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "display_name")
    private String displayName;

    private String description;

    @Column(nullable = false)
    private String type; // text, number, select, boolean 등

    @Column(nullable = false)
    private boolean required;

    @Column(nullable = false)
    private boolean filterable;

    @Column(nullable = false)
    private boolean searchable;

    @Column(nullable = false)
    private boolean comparable;

    @Column(nullable = false)
    private boolean showInProductListing;

    private Integer sortOrder;

    @Column(columnDefinition = "boolean default true")
    private boolean enabled;

    @OneToMany(mappedBy = "attributeOption", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductAttribute> attributes = new ArrayList<>();

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public void addAttribute(ProductAttribute attribute) {
        attributes.add(attribute);
        attribute.setAttributeOption(this);
    }

    public void removeAttribute(ProductAttribute attribute) {
        attributes.remove(attribute);
        attribute.setAttributeOption(null);
    }
} 