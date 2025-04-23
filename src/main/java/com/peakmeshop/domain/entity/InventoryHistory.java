package com.peakmeshop.domain.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "inventory_history")
@Getter
@Setter
@NoArgsConstructor
public class InventoryHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "inventory_id", nullable = false)
    private Inventory inventory;

    @Column(nullable = false)
    private int quantityBefore;

    @Column(nullable = false)
    private int quantityAfter;

    @Column(nullable = false)
    private int quantityChanged;

    @Column(nullable = false)
    private String reason;

    @Column(nullable = false)
    private String actionType; // INCREASE, DECREASE, RESERVE, RELEASE, CONFIRM

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Member member;

    @Column
    private Long orderId;

    @Column(nullable = false)
    private LocalDateTime createdAt;
}