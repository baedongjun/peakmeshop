package com.peakmeshop.api.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.peakmeshop.domain.entity.SupplierProduct;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupplierDTO {

    private Long id;
    private String code;
    private String name;
    private String businessNumber;
    private String representativeName;
    private String phone;
    private String email;
    private String zipCode;
    private String address1;
    private String address2;
    private String bankName;
    private String accountNumber;
    private String accountHolder;
    private String city;
    private String state;

    private String country;
    private String website;
    private String description;
    private String status;
    private Boolean isActive;
    private Long productCount;
    private Long totalSales;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<SupplierProduct> products;
}