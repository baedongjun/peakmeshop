package com.peakmeshop.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacetDTO {
    private String id;
    private String name;
    private List<FacetValueDTO> values;
}