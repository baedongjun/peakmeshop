package com.peakmeshop.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacetValueDTO {
    private String id;
    private String name;
    private int count;
    private boolean selected;
}