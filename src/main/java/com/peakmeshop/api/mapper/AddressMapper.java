package com.peakmeshop.api.mapper;

import com.peakmeshop.domain.entity.Address;
import com.peakmeshop.api.dto.AddressDTO;
import org.mapstruct.*;

@Mapper(
    componentModel = "spring",
    uses = {BaseMapper.class},
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface AddressMapper {
    
    @Mapping(target = "memberId", source = "member.id")
    AddressDTO toDTO(Address address);

    @Mapping(target = "member", ignore = true)
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    Address toEntity(AddressDTO dto);
} 