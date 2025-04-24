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
    
    @Mapping(target = "memberId", source = "member.id")
    @Mapping(target = "userId", source = "member.userId")
    ActivityLogDTO toDTO(ActivityLog activityLog);

    @Mapping(target = "member", ignore = true)
    ActivityLog toEntity(ActivityLogDTO dto);
} 