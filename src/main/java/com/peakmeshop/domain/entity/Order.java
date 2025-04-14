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

    @Column(name = "order_number", unique = true)
    private String orderNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Column(name = "total_price", precision = 10)
    private BigDecimal totalPrice;

    @Column(precision = 10)
    private BigDecimal discount;

    @Column(name = "delivery_fee", precision = 10)
    private BigDecimal deliveryFee;

    @Column(name = "final_price", precision = 10)
    private Double finalPrice;

    @Column(name = "recipient_name")
    private String recipientName;
    @Column(name = "recipient_tel")
    private String recipientTel;
    @Column(name = "recipient_address")
    private String recipientAddress;
    @Column(name = "recipient_detail_address")
    private String recipientDetailAddress;
    @Column(name = "recipient_message")
    private String recipientMessage;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Column(name = "payment_status")
    private String paymentStatus;
    @Column(name = "payment_transaction_id")
    private String paymentTransactionId;
    @Column(name = "shipping_method")
    private String shippingMethod;
    @Column(name = "tracking_number")
    private String trackingNumber;
    @Column(name = "shipping_company")
    private String shippingCompany;
    @Column(name = "cancel_reason")
    private String cancelReason;
    @Column(name = "refund_reason")
    private String refundReason;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;
    @Column(name = "shipped_at")
    private LocalDateTime shippedAt;
    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;
    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;
    @Column(name = "refunded_at")
    private LocalDateTime refundedAt;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    @Column(name = "completed_at")
    private LocalDateTime completedAt;

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

    public void updateStatus(
            OrderStatus orderStatus) {
        this.status = orderStatus;
    }
}