package com.peakmeshop.api.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PointDTO {

    private Long id;
    private Long memberId;
    private String memberName;
    private String memberEmail;
    private Integer currentPoint;
    private Integer totalEarnedPoint;
    private Integer totalUsedPoint;
    private LocalDateTime updatedAt;
}