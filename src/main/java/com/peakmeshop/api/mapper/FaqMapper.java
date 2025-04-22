package com.peakmeshop.api.mapper;

import com.peakmeshop.domain.entity.Faq;
import com.peakmeshop.api.dto.FaqDTO;
import org.mapstruct.*;

@Mapper(
    componentModel = "spring",
    uses = {BaseMapper.class},
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface FaqMapper {
    
    FaqDTO toDTO(Faq faq);

    Faq toEntity(FaqDTO dto);
} 