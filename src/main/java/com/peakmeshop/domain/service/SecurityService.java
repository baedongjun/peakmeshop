package com.peakmeshop.domain.service;

import com.peakmeshop.api.dto.MemberDTO;

public interface SecurityService {

    /**
     * 현재 인증된 사용자가 요청한 회원 ID와 일치하는지 확인합니다.
     *
     * @param memberId 회원 ID
     * @return 일치 여부
     */
    boolean isCurrentUser(Long memberId);

    /**
     * 현재 인증된 사용자의 정보를 반환합니다.
     *
     * @return 현재 사용자 정보 (인증되지 않은 경우 null)
     */
    MemberDTO getCurrentUser();
}