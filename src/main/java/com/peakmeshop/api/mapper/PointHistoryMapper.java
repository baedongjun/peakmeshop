package com.peakmeshop.api.mapper;

import com.peakmeshop.domain.entity.PointHistory;
import com.peakmeshop.api.dto.PointHistoryDTO;
import org.mapstruct.*;

@Mapper(
    componentModel = "spring",
    uses = {BaseMapper.class, MemberMapper.class},
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface PointHistoryMapper {
    
    @Mapping(target = "memberId", source = "member.id")
    PointHistoryDTO toDTO(PointHistory pointHistory);

    @Mapping(target = "member", ignore = true)
    PointHistory toEntity(PointHistoryDTO dto);
} 