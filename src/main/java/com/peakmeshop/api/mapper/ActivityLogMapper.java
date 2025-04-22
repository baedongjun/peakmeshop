package com.peakmeshop.api.mapper;

import com.peakmeshop.domain.entity.ActivityLog;
import com.peakmeshop.api.dto.ActivityLogDTO;
import org.mapstruct.*;

@Mapper(
    componentModel = "spring",
    uses = {BaseMapper.class},
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface ActivityLogMapper {
    
    ActivityLogDTO toDTO(ActivityLog activityLog);

    ActivityLog toEntity(ActivityLogDTO dto);
} 