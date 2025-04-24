package com.peakmeshop.api.mapper;

import com.peakmeshop.domain.entity.Inquiry;
import com.peakmeshop.api.dto.InquiryDTO;
import org.mapstruct.*;

@Mapper(
    componentModel = "spring",
    uses = {BaseMapper.class},
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface InquiryMapper {
    
    InquiryDTO toDTO(Inquiry inquiry);

    Inquiry toEntity(InquiryDTO dto);
} 