package com.peakmeshop.api.mapper;

import com.peakmeshop.domain.entity.Statistics;
import com.peakmeshop.api.dto.StatisticsDTO;
import org.mapstruct.*;

@Mapper(
    componentModel = "spring",
    uses = {BaseMapper.class},
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface StatisticsMapper {
    
    StatisticsDTO toDTO(Statistics statistics);

    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    Statistics toEntity(StatisticsDTO dto);
} 