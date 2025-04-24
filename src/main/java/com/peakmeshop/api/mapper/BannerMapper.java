package com.peakmeshop.api.mapper;

import com.peakmeshop.api.dto.BannerDTO;
import com.peakmeshop.domain.entity.Banner;

import org.mapstruct.*;

@Mapper(
    componentModel = "spring",
    uses = {BaseMapper.class},
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface BannerMapper {
    
    BannerDTO toDTO(Banner banner);

    Banner toEntity(BannerDTO dto);
} 