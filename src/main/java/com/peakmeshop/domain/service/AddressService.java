package com.peakmeshop.domain.service;

import java.util.List;

import com.peakmeshop.api.dto.AddressDTO;

public interface AddressService {

    AddressDTO getAddressById(Long id);

    AddressDTO getAddressById(Long memberId, Long addressId);

    List<AddressDTO> getAddressesByMemberId(Long memberId);

    List<AddressDTO> getAddressesByUserId(String userId);

    AddressDTO createAddress(AddressDTO addressDTO);

    AddressDTO updateAddress(AddressDTO addressDTO);

    void deleteAddress(Long id);

    void deleteAddress(Long memberId, Long addressId);

    void setDefaultAddress(Long memberId, Long addressId);
}