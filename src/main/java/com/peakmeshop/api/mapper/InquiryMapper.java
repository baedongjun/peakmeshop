package com.peakmeshop.api.mapper;

import com.peakmeshop.api.dto.InquiryDTO;
import com.peakmeshop.domain.entity.Inquiry;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {MemberMapper.class, ProductMapper.class})
public interface InquiryMapper {
    
    InquiryDTO toDto(Inquiry inquiry);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Inquiry toEntity(InquiryDTO inquiryDTO);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateInquiryFromDto(InquiryDTO inquiryDTO, @MappingTarget Inquiry inquiry);
} 