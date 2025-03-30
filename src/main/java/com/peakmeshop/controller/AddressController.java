package com.peakmeshop.controller;

import com.peakmeshop.dto.AddressDTO;
import com.peakmeshop.security.oauth2.user.UserPrincipal;
import com.peakmeshop.service.AddressService;
import com.peakmeshop.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;
    private final MemberService memberService;

    @GetMapping("/member/{userId}")
    public ResponseEntity<List<AddressDTO>> getAddressesByUserId(@PathVariable String userId) {
        // 사용자 ID로 회원 조회
        memberService.getMemberByUserId(userId);

        List<AddressDTO> addresses = addressService.getAddressesByUserId(userId);
        return ResponseEntity.ok(addresses);
    }

    @GetMapping("/my-addresses")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<AddressDTO>> getMyAddresses(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        List<AddressDTO> addresses = addressService.getAddressesByMemberId(userPrincipal.getId());
        return ResponseEntity.ok(addresses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AddressDTO> getAddressById(@PathVariable Long id) {
        AddressDTO address = addressService.getAddressById(id);
        return ResponseEntity.ok(address);
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<AddressDTO> createAddress(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody AddressDTO addressDTO) {

        addressDTO.setMemberId(userPrincipal.getId());
        AddressDTO createdAddress = addressService.createAddress(addressDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAddress);
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<AddressDTO> updateAddress(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody AddressDTO addressDTO) {

        // 주소 소유자 확인
        AddressDTO existingAddress = addressService.getAddressById(id);
        if (!existingAddress.getMemberId().equals(userPrincipal.getId()) && !userPrincipal.isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        addressDTO.setId(id);
        addressDTO.setMemberId(existingAddress.getMemberId());

        AddressDTO updatedAddress = addressService.updateAddress(addressDTO);
        return ResponseEntity.ok(updatedAddress);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteAddress(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        // 주소 소유자 확인
        AddressDTO existingAddress = addressService.getAddressById(id);
        if (!existingAddress.getMemberId().equals(userPrincipal.getId()) && !userPrincipal.isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        addressService.deleteAddress(id);
        return ResponseEntity.noContent().build();
    }
}