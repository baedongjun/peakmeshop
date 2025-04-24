package com.peakmeshop.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "statistics")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Statistics {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String type;
    
    @Column(nullable = false)
    private LocalDate date;
    
    @Column(nullable = false)
    private double totalSales;
    
    @Column(nullable = false)
    private int totalOrders;
    
    @Column(nullable = false)
    private int totalProducts;
    
    @Column(nullable = false)
    private double averageOrderValue;
    
    @Column(nullable = false)
    private int newCustomers;
    
    @Column(nullable = false)
    private int returningCustomers;
    
    @Column(nullable = false)
    private double conversionRate;
    
    @ElementCollection
    @CollectionTable(name = "statistics_sales_by_category", 
            joinColumns = @JoinColumn(name = "statistics_id"))
    @MapKeyColumn(name = "category")
    @Column(name = "sales")
    private Map<String, Double> salesByCategory;
    
    @ElementCollection
    @CollectionTable(name = "statistics_sales_by_product", 
            joinColumns = @JoinColumn(name = "statistics_id"))
    @MapKeyColumn(name = "product")
    @Column(name = "sales")
    private Map<String, Double> salesByProduct;
    
    @ElementCollection
    @CollectionTable(name = "statistics_orders_by_status", 
            joinColumns = @JoinColumn(name = "statistics_id"))
    @MapKeyColumn(name = "status")
    @Column(name = "count")
    private Map<String, Integer> ordersByStatus;
    
    @ElementCollection
    @CollectionTable(name = "statistics_sales_by_payment_method", 
            joinColumns = @JoinColumn(name = "statistics_id"))
    @MapKeyColumn(name = "payment_method")
    @Column(name = "sales")
    private Map<String, Double> salesByPaymentMethod;
    
    @ElementCollection
    @CollectionTable(name = "statistics_sales_by_region", 
            joinColumns = @JoinColumn(name = "statistics_id"))
    @MapKeyColumn(name = "region")
    @Column(name = "sales")
    private Map<String, Double> salesByRegion;
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
} 