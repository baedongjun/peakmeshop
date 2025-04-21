package com.peakmeshop.api.mapper;

import com.peakmeshop.api.dto.FaqDTO;
import com.peakmeshop.domain.entity.Faq;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface FaqMapper {
    
    FaqMapper INSTANCE = Mappers.getMapper(FaqMapper.class);
    
    FaqDTO toDto(Faq faq);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Faq toEntity(FaqDTO faqDTO);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateFaqFromDto(FaqDTO faqDTO, @MappingTarget Faq faq);
} 