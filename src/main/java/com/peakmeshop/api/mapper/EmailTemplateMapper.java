package com.peakmeshop.api.mapper;

import com.peakmeshop.domain.entity.EmailTemplate;
import com.peakmeshop.api.dto.EmailTemplateDTO;
import org.mapstruct.*;

@Mapper(
    componentModel = "spring",
    uses = {BaseMapper.class},
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface EmailTemplateMapper {
    
    EmailTemplateDTO toDTO(EmailTemplate emailTemplate);

    EmailTemplate toEntity(EmailTemplateDTO dto);
} 