package com.peakmeshop.api.mapper;

import com.peakmeshop.api.dto.SettingDTO;
import com.peakmeshop.domain.entity.Setting;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SettingMapper {
    
    SettingDTO toDTO(Setting setting);
    
    Setting toEntity(SettingDTO settingDTO);
} 