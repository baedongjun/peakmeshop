package com.peakmeshop.api.mapper;

import com.peakmeshop.api.dto.AddressDTO;
import com.peakmeshop.domain.entity.Address;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import java.util.List;

@Mapper(componentModel = "spring")
public interface AddressMapper {
    
    @Mapping(source = "member.id", target = "memberId")
    AddressDTO toDto(Address address);
    
    List<AddressDTO> toDtoList(List<Address> addresses);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(source = "memberId", target = "member.id")
    Address toEntity(AddressDTO addressDTO);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(source = "memberId", target = "member.id")
    void updateAddressFromDto(AddressDTO addressDTO, @MappingTarget Address address);
} 