package com.peakmeshop.api.mapper;

import com.peakmeshop.domain.entity.Popup;
import com.peakmeshop.api.dto.PopupDTO;
import org.mapstruct.*;

@Mapper(
    componentModel = "spring",
    uses = {BaseMapper.class},
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface PopupMapper {
    
    PopupDTO toDTO(Popup popup);

    Popup toEntity(PopupDTO dto);
} 