package com.peakmeshop.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "product_bundle_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductBundleItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bundle_id", nullable = false)
    private ProductBundle bundle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private int quantity;

    @Column
    private BigDecimal discountRate;

    @Column
    private BigDecimal discountAmount;

    public BigDecimal getTotalPrice() {
        BigDecimal productPrice = product.getFinalPrice();
        return productPrice.multiply(BigDecimal.valueOf(quantity));
    }

    public BigDecimal getDiscountedPrice() {
        BigDecimal totalPrice = getTotalPrice();

        if (discountAmount != null) {
            return totalPrice.subtract(discountAmount);
        }

        if (discountRate != null) {
            BigDecimal discountValue = totalPrice.multiply(discountRate).divide(new BigDecimal(100));
            return totalPrice.subtract(discountValue);
        }

        return totalPrice;
    }
}