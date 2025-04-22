package com.peakmeshop.api.mapper;

import com.peakmeshop.domain.entity.Point;
import com.peakmeshop.api.dto.PointDTO;
import org.mapstruct.*;

@Mapper(
    componentModel = "spring",
    uses = {BaseMapper.class},
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface PointMapper {
    
    @Mapping(target = "memberId", source = "member.id")
    @Mapping(target = "memberName", source = "member.name")
    PointDTO toDTO(Point point);

    @Mapping(target = "member", ignore = true)
    Point toEntity(PointDTO dto);
} 