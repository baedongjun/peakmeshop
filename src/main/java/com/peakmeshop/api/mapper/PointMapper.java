package com.peakmeshop.api.mapper;

import com.peakmeshop.api.dto.PointDTO;
import com.peakmeshop.domain.entity.Point;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {MemberMapper.class})
public interface PointMapper {
    
    PointDTO toDto(Point point);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Point toEntity(PointDTO pointDTO);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updatePointFromDto(PointDTO pointDTO, @MappingTarget Point point);
} 