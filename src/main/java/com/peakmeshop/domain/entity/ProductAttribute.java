package com.peakmeshop.domain.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.CollectionTable;
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
@Table(name = "product_attributes")
@Getter
@Setter
@NoArgsConstructor
public class ProductAttribute {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String code;

    @Column
    private String description;

    @Column(nullable = false)
    private String type; // 예: text, number, select, boolean 등

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

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "product_attribute_options", joinColumns = @JoinColumn(name = "attribute_id"))
    @Column(name = "option_value")
    private List<String> options = new ArrayList<>();

    // Product와의 관계 추가
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}