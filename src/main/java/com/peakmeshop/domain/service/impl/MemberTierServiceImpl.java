package com.peakmeshop.domain.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.peakmeshop.api.dto.MemberTierDTO;
import com.peakmeshop.domain.entity.MemberTier;
import com.peakmeshop.common.exception.BadRequestException;
import com.peakmeshop.domain.repository.MemberTierRepository;
import com.peakmeshop.domain.service.MemberTierService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberTierServiceImpl implements MemberTierService {

    private final MemberTierRepository memberTierRepository;

    @Override
    @Transactional
    public MemberTierDTO createMemberTier(MemberTierDTO memberTierDTO) {
        // 코드 중복 확인
        if (memberTierRepository.existsByCode(memberTierDTO.getCode())) {
            throw new BadRequestException("이미 존재하는 등급 코드입니다: " + memberTierDTO.getCode());
        }

        // 순서 설정 (기본값: 마지막 순서 + 1)
        if (memberTierDTO.getOrderIndex() == null) {
            Integer maxOrder = memberTierRepository.findMaxOrderIndex();
            memberTierDTO.setOrderIndex(maxOrder != null ? maxOrder + 1 : 1);
        }

        MemberTier memberTier = convertToEntity(memberTierDTO);
        MemberTier savedMemberTier = memberTierRepository.save(memberTier);

        return convertToDTO(savedMemberTier);
    }

    @Override
    @Transactional(readOnly = true)
    public MemberTierDTO getMemberTierById(Long id) {
        MemberTier memberTier = memberTierRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Member tier not found with id: " + id));

        return convertToDTO(memberTier);
    }

    @Override
    @Transactional(readOnly = true)
    public MemberTierDTO getMemberTierByCode(String code) {
        MemberTier memberTier = memberTierRepository.findByCode(code)
                .orElseThrow(() -> new UsernameNotFoundException("Member tier not found with code: " + code));

        return convertToDTO(memberTier);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MemberTierDTO> getAllMemberTiers() {
        return memberTierRepository.findAll(Sort.by(Sort.Direction.ASC, "orderIndex")).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MemberTierDTO> getAllMemberTiers(Pageable pageable) {
        return memberTierRepository.findAll(pageable)
                .map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MemberTierDTO> getActiveMemberTiers() {
        return memberTierRepository.findByIsActiveTrue(Sort.by(Sort.Direction.ASC, "orderIndex")).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public MemberTierDTO updateMemberTier(Long id, MemberTierDTO memberTierDTO) {
        MemberTier memberTier = memberTierRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Member tier not found with id: " + id));

        // 코드 변경 시 중복 확인
        if (!memberTier.getCode().equals(memberTierDTO.getCode()) &&
                memberTierRepository.existsByCode(memberTierDTO.getCode())) {
            throw new BadRequestException("이미 존재하는 등급 코드입니다: " + memberTierDTO.getCode());
        }

        // 기존 정보 업데이트
        memberTier.setName(memberTierDTO.getName());
        memberTier.setCode(memberTierDTO.getCode());
        memberTier.setDescription(memberTierDTO.getDescription());
        memberTier.setMinimumSpend(memberTierDTO.getMinimumSpend());
        memberTier.setDiscountRate(memberTierDTO.getDiscountRate());
        memberTier.setPointRate(memberTierDTO.getPointRate());
        memberTier.setOrderIndex(memberTierDTO.getOrderIndex());
        memberTier.setIsActive(memberTierDTO.getIsActive());

        MemberTier updatedMemberTier = memberTierRepository.save(memberTier);
        return convertToDTO(updatedMemberTier);
    }

    @Override
    @Transactional
    public void deleteMemberTier(Long id) {
        MemberTier memberTier = memberTierRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Member tier not found with id: " + id));

        // 회원이 사용 중인 등급인지 확인
        if (memberTier.getMembers() != null && !memberTier.getMembers().isEmpty()) {
            throw new BadRequestException("회원이 사용 중인 등급은 삭제할 수 없습니다.");
        }

        memberTierRepository.delete(memberTier);
    }

    @Override
    @Transactional
    public MemberTierDTO toggleTierStatus(Long id, Boolean active) {
        MemberTier memberTier = memberTierRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Member tier not found with id: " + id));

        memberTier.setIsActive(active);
        MemberTier updatedMemberTier = memberTierRepository.save(memberTier);

        return convertToDTO(updatedMemberTier);
    }

    @Override
    @Transactional
    public int updateTierOrder(List<MemberTierDTO> tierOrders) {
        int updatedCount = 0;

        for (MemberTierDTO tierOrder : tierOrders) {
            MemberTier memberTier = memberTierRepository.findById(tierOrder.getId())
                    .orElseThrow(() -> new UsernameNotFoundException("Member tier not found with id: " + tierOrder.getId()));

            memberTier.setOrderIndex(tierOrder.getOrderIndex());
            memberTierRepository.save(memberTier);
            updatedCount++;
        }

        return updatedCount;
    }

    // MemberTier 엔티티를 MemberTierDTO로 변환하는 메서드
    private MemberTierDTO convertToDTO(MemberTier memberTier) {
        return MemberTierDTO.builder()
                .id(memberTier.getId())
                .name(memberTier.getName())
                .code(memberTier.getCode())
                .description(memberTier.getDescription())
                .minimumSpend(memberTier.getMinimumSpend())
                .discountRate(memberTier.getDiscountRate())
                .pointRate(memberTier.getPointRate())
                .orderIndex(memberTier.getOrderIndex())
                .isActive(memberTier.getIsActive())
                .createdAt(memberTier.getCreatedAt())
                .updatedAt(memberTier.getUpdatedAt())
                .build();
    }

    // MemberTierDTO를 MemberTier 엔티티로 변환하는 메서드
    private MemberTier convertToEntity(MemberTierDTO memberTierDTO) {
        return MemberTier.builder()
                .id(memberTierDTO.getId())
                .name(memberTierDTO.getName())
                .code(memberTierDTO.getCode())
                .description(memberTierDTO.getDescription())
                .minimumSpend(memberTierDTO.getMinimumSpend())
                .discountRate(memberTierDTO.getDiscountRate())
                .pointRate(memberTierDTO.getPointRate())
                .orderIndex(memberTierDTO.getOrderIndex())
                .isActive(memberTierDTO.getIsActive())
                .build();
    }
}