package com.peakmeshop.domain.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.peakmeshop.api.dto.MemberTierDTO;

public interface MemberTierService {

    /**
     * 새로운 회원 등급을 생성합니다.
     *
     * @param memberTierDTO 회원 등급 정보
     * @return 생성된 회원 등급 정보
     */
    MemberTierDTO createMemberTier(MemberTierDTO memberTierDTO);

    /**
     * 회원 등급 ID로 회원 등급 정보를 조회합니다.
     *
     * @param id 회원 등급 ID
     * @return 회원 등급 정보
     */
    MemberTierDTO getMemberTierById(Long id);

    /**
     * 회원 등급 코드로 회원 등급 정보를 조회합니다.
     *
     * @param code 회원 등급 코드
     * @return 회원 등급 정보
     */
    MemberTierDTO getMemberTierByCode(String code);

    /**
     * 모든 회원 등급 정보를 조회합니다.
     *
     * @return 회원 등급 정보 목록
     */
    List<MemberTierDTO> getAllMemberTiers();

    /**
     * 모든 회원 등급 정보를 페이징하여 조회합니다.
     *
     * @param pageable 페이징 정보
     * @return 회원 등급 정보 목록
     */
    Page<MemberTierDTO> getAllMemberTiers(Pageable pageable);

    /**
     * 활성화된 회원 등급 정보를 조회합니다.
     *
     * @return 활성화된 회원 등급 정보 목록
     */
    List<MemberTierDTO> getActiveMemberTiers();

    /**
     * 회원 등급 정보를 업데이트합니다.
     *
     * @param id 회원 등급 ID
     * @param memberTierDTO 업데이트할 회원 등급 정보
     * @return 업데이트된 회원 등급 정보
     */
    MemberTierDTO updateMemberTier(Long id, MemberTierDTO memberTierDTO);

    /**
     * 회원 등급을 삭제합니다.
     *
     * @param id 회원 등급 ID
     */
    void deleteMemberTier(Long id);

    /**
     * 회원 등급 활성화 상태를 토글합니다.
     *
     * @param id 회원 등급 ID
     * @param active 활성화 여부
     * @return 업데이트된 회원 등급 정보
     */
    MemberTierDTO toggleTierStatus(Long id, Boolean active);

    /**
     * 회원 등급 순서를 업데이트합니다.
     *
     * @param tierOrders 회원 등급 ID와 순서 맵
     * @return 업데이트된 회원 등급 수
     */
    int updateTierOrder(List<MemberTierDTO> tierOrders);
}