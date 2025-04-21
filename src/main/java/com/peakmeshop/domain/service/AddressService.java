package com.peakmeshop.domain.service;

import java.util.List;
import com.peakmeshop.api.dto.AddressDTO;

public interface AddressService {
    
    AddressDTO getAddressById(Long id);
    
    AddressDTO getAddressById(Long memberId, Long addressId);
    
    List<AddressDTO> getAddressesByMemberId(Long memberId);
    
    List<AddressDTO> getAddressesByUserId(String userId);
    
    AddressDTO createAddress(String userId, AddressDTO addressDTO);
    
    AddressDTO updateAddress(String userId, Long addressId, AddressDTO addressDTO);
    
    void deleteAddress(String userId, Long addressId);
    
    void setDefaultAddress(Long memberId, Long addressId);
}