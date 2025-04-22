package com.peakmeshop.api.dto;

import com.peakmeshop.domain.entity.RoleName;
import com.peakmeshop.domain.enums.OrderStatus;
import com.peakmeshop.domain.enums.RefundStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleDTO {
    private Long id;
    private RoleName name;
    private String description;
}