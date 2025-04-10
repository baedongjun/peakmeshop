package com.peakmeshop.domain.entity;

import com.peakmeshop.domain.enums.OrderStatus;
import com.peakmeshop.domain.enums.PaymentMethod;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String orderNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Column(precision = 10, scale = 2)
    private BigDecimal totalPrice;

    @Column(precision = 10, scale = 2)
    private BigDecimal discount;

    @Column(precision = 10, scale = 2)
    private BigDecimal deliveryFee;

    @Column(precision = 10, scale = 2)
    private Double finalPrice;

    private String recipientName;
    private String recipientTel;
    private String recipientAddress;
    private String recipientDetailAddress;
    private String recipientMessage;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    private String paymentStatus;
    private String paymentTransactionId;
    private String shippingMethod;
    private String trackingNumber;
    private String shippingCompany;
    private String cancelReason;
    private String refundReason;

    private LocalDateTime paidAt;
    private LocalDateTime shippedAt;
    private LocalDateTime deliveredAt;
    private LocalDateTime cancelledAt;
    private LocalDateTime refundedAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    public List<OrderItem> getOrderItems() {
        return items;
    }

    public void addItem(OrderItem item) {
        items.add(item);
        item.setOrder(this);
    }

    public void removeItem(OrderItem item) {
        items.remove(item);
        item.setOrder(null);
    }

    public BigDecimal getSubtotal() {
        return totalPrice;
    }

    public BigDecimal getDiscountAmount() {
        return discount;
    }

    public BigDecimal getShippingCost() {
        return deliveryFee;
    }

    public BigDecimal getTax() {
        return BigDecimal.ZERO;
    }

    public Double getTotalAmount() {
        return finalPrice;
    }

    public BigDecimal getTotalSales() {
        return items.stream()
                .map(OrderItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTotalProfit() {
        return items.stream()
                .map(OrderItem::getProfit)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}