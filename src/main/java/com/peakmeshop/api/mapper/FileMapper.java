package com.peakmeshop.api.mapper;

import com.peakmeshop.domain.entity.File;
import com.peakmeshop.api.dto.FileDTO;
import org.mapstruct.*;

@Mapper(
    componentModel = "spring",
    uses = {BaseMapper.class},
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface FileMapper {
    
    FileDTO toDTO(File file);

    File toEntity(FileDTO dto);
} 