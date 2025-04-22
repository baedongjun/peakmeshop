package com.peakmeshop.api.mapper;

import com.peakmeshop.domain.entity.Notice;
import com.peakmeshop.api.dto.NoticeDTO;
import org.mapstruct.*;

@Mapper(
    componentModel = "spring",
    uses = {BaseMapper.class},
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface NoticeMapper {
    
    NoticeDTO toDTO(Notice notice);

    Notice toEntity(NoticeDTO dto);
} 