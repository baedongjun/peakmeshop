package com.peakmeshop.api.mapper;

import com.peakmeshop.api.dto.NoticeDTO;
import com.peakmeshop.domain.entity.Notice;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface NoticeMapper {
    
    NoticeDTO toDto(Notice notice);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "viewCount", ignore = true)
    Notice toEntity(NoticeDTO noticeDTO);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "viewCount", ignore = true)
    void updateNoticeFromDto(NoticeDTO noticeDTO, @MappingTarget Notice notice);
} 