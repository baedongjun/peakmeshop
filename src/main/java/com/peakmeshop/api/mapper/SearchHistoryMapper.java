package com.peakmeshop.api.mapper;

import com.peakmeshop.domain.entity.SearchHistory;
import com.peakmeshop.api.dto.SearchHistoryDTO;
import org.mapstruct.*;

@Mapper(
    componentModel = "spring",
    uses = {BaseMapper.class},
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface SearchHistoryMapper {
    
    @Mapping(target = "memberId", source = "member.id")
    SearchHistoryDTO toDTO(SearchHistory searchHistory);

    @Mapping(target = "member", ignore = true)
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    SearchHistory toEntity(SearchHistoryDTO dto);
} 