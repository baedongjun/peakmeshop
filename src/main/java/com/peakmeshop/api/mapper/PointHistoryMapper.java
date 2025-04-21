package com.peakmeshop.api.mapper;

import com.peakmeshop.api.dto.PointHistoryDTO;
import com.peakmeshop.domain.entity.PointHistory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {MemberMapper.class, OrderMapper.class})
public interface PointHistoryMapper {
    
    PointHistoryDTO toDto(PointHistory pointHistory);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    PointHistory toEntity(PointHistoryDTO pointHistoryDTO);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updatePointHistoryFromDto(PointHistoryDTO pointHistoryDTO, @MappingTarget PointHistory pointHistory);
} 