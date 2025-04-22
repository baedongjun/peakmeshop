package com.peakmeshop.api.mapper;

import com.peakmeshop.domain.entity.Email;
import com.peakmeshop.api.dto.EmailDTO;
import org.mapstruct.*;

@Mapper(
    componentModel = "spring",
    uses = {BaseMapper.class, EmailTemplateMapper.class},
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface EmailMapper {
    
    @Mapping(target = "templateId", source = "template.id")
    EmailDTO toDTO(Email email);

    @Mapping(target = "template", ignore = true)
    Email toEntity(EmailDTO dto);
} 