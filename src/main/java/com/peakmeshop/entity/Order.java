package com.peakmeshop.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.peakmeshop.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String orderNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipping_address_id")
    private Address shippingAddress;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "billing_address_id")
    private Address billingAddress;
    @Enumerated(EnumType.ORDINAL)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(nullable = false)
    private BigDecimal subtotal;

    @Column
    private BigDecimal discountAmount;

    @Column
    private BigDecimal shippingCost;

    @Column
    private BigDecimal tax;

    @Column(nullable = false)
    private BigDecimal totalAmount;

    @Column
    private String paymentMethod;

    @Column
    private String paymentStatus;

    @Column
    private String paymentTransactionId;

    @Column
    private String shippingMethod;

    @Column
    private String trackingNumber;

    @Column
    private String shippingCompany;

    @Column
    private String cancelReason;

    @Column
    private String refundReason;

    @Column
    private LocalDateTime paidAt;

    @Column
    private LocalDateTime shippedAt;

    @Column
    private LocalDateTime deliveredAt;

    @Column
    private LocalDateTime cancelledAt;

    @Column
    private LocalDateTime refundedAt;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    public BigDecimal getTotalAmount() {
        if (totalAmount != null) {
            return totalAmount;
        }

        // 주문 항목이 없는 경우
        if (items == null || items.isEmpty()) {
            return BigDecimal.ZERO;
        }

        // 주문 항목의 총 금액 계산
        BigDecimal total = items.stream()
                .map(item -> {
                    BigDecimal itemPrice = item.getPrice().multiply(new BigDecimal(item.getQuantity()));
                    if (item.getDiscount() != null) {
                        itemPrice = itemPrice.subtract(item.getDiscount());
                    }
                    return itemPrice;
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 배송비 추가
        if (shippingCost != null) {
            total = total.add(shippingCost);
        }

        // 세금 추가
        if (tax != null) {
            total = total.add(tax);
        }

        // 할인 적용
        if (discountAmount != null) {
            total = total.subtract(discountAmount);
        }

        return total;
    }

    public List<OrderItem> getItems() {
        return items;
    }
}