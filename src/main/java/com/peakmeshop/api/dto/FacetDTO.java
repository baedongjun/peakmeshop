package com.peakmeshop.api.dto;

import java.util.List;

import com.peakmeshop.api.dto.FacetValueDTO;
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