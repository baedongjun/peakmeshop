package com.peakmeshop.api.mapper;

import com.peakmeshop.domain.entity.Email;
import com.peakmeshop.api.dto.EmailDTO;
import org.mapstruct.*;

@Mapper(
    componentModel = "spring",
    uses = {BaseMapper.class},
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface EmailMapper {
    
    EmailDTO toDTO(Email email);

    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    Email toEntity(EmailDTO dto);
} 