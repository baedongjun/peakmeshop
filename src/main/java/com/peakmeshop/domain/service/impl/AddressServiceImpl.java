package com.peakmeshop.domain.service.impl;

import com.peakmeshop.api.dto.AddressDTO;
import com.peakmeshop.domain.entity.Address;
import com.peakmeshop.domain.entity.Member;
import com.peakmeshop.common.exception.ResourceNotFoundException;
import com.peakmeshop.domain.repository.AddressRepository;
import com.peakmeshop.domain.repository.MemberRepository;
import com.peakmeshop.domain.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final MemberRepository memberRepository;

    @Override
    @Transactional(readOnly = true)
    public AddressDTO getAddressById(Long id) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found with id: " + id));
        return convertToDTO(address);
    }

    @Override
    @Transactional(readOnly = true)
    public AddressDTO getAddressById(Long memberId, Long addressId) {
        Address address = addressRepository.findByIdAndMemberId(addressId, memberId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Address not found with id: " + addressId + " for member id: " + memberId));
        return convertToDTO(address);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AddressDTO> getAddressesByMemberId(Long memberId) {
        List<Address> addresses = addressRepository.findByMemberId(memberId);
        return addresses.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AddressDTO> getAddressesByUserId(String userId) {
        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with userId: " + userId));

        List<Address> addresses = addressRepository.findByMemberId(member.getId());
        return addresses.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AddressDTO createAddress(AddressDTO addressDTO) {
        Member member = memberRepository.findById(addressDTO.getMemberId())
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with id: " + addressDTO.getMemberId()));

        // 첫 번째 주소인 경우 기본 주소로 설정
        boolean isDefault = !addressRepository.existsByMemberId(addressDTO.getMemberId());

        Address address = Address.builder()
                .member(member)
                .recipientName(addressDTO.getRecipientName())
                .zipcode(addressDTO.getZipcode())
                .address1(addressDTO.getAddress1())
                .address2(addressDTO.getAddress2())
                .phone(addressDTO.getPhone())
                .isDefault(isDefault)
                .createdAt(LocalDateTime.now())
                .build();

        Address savedAddress = addressRepository.save(address);
        return convertToDTO(savedAddress);
    }

    @Override
    @Transactional
    public AddressDTO updateAddress(AddressDTO addressDTO) {
        Address address = addressRepository.findById(addressDTO.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Address not found with id: " + addressDTO.getId()));

        address.setRecipientName(addressDTO.getRecipientName());
        address.setZipcode(addressDTO.getZipcode());
        address.setAddress1(addressDTO.getAddress1());
        address.setAddress2(addressDTO.getAddress2());
        address.setPhone(addressDTO.getPhone());
        address.setUpdatedAt(LocalDateTime.now());

        Address updatedAddress = addressRepository.save(address);
        return convertToDTO(updatedAddress);
    }

    @Override
    @Transactional
    public void deleteAddress(Long id) {
        if (!addressRepository.existsById(id)) {
            throw new ResourceNotFoundException("Address not found with id: " + id);
        }

        addressRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteAddress(Long memberId, Long addressId) {
        Address address = addressRepository.findByIdAndMemberId(addressId, memberId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Address not found with id: " + addressId + " for member id: " + memberId));

        addressRepository.delete(address);

        // 삭제된 주소가 기본 주소였다면 다른 주소를 기본 주소로 설정
        if (address.isDefault()) {
            addressRepository.findFirstByMemberIdOrderByCreatedAtAsc(memberId)
                    .ifPresent(newDefaultAddress -> {
                        newDefaultAddress.setDefault(true);
                        addressRepository.save(newDefaultAddress);
                    });
        }
    }

    @Override
    @Transactional
    public void setDefaultAddress(Long memberId, Long addressId) {
        // 기존 기본 주소 해제
        addressRepository.findByMemberIdAndIsDefaultTrue(memberId)
                .ifPresent(oldDefault -> {
                    oldDefault.setDefault(false);
                    addressRepository.save(oldDefault);
                });

        // 새 기본 주소 설정
        Address newDefault = addressRepository.findByIdAndMemberId(addressId, memberId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Address not found with id: " + addressId + " for member id: " + memberId));

        newDefault.setDefault(true);
        addressRepository.save(newDefault);
    }

    private AddressDTO convertToDTO(Address address) {
        return AddressDTO.builder()
                .id(address.getId())
                .memberId(address.getMember().getId())
                .recipientName(address.getRecipientName())
                .zipcode(address.getZipcode())
                .address1(address.getAddress1())
                .address2(address.getAddress2())
                .phone(address.getPhone())
                .isDefault(address.isDefault())
                .createdAt(address.getCreatedAt())
                .updatedAt(address.getUpdatedAt())
                .build();
    }
}