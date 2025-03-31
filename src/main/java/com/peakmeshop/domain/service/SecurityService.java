package com.peakmeshop.domain.service;

public interface SecurityService {

    /**
     * 현재 인증된 사용자가 요청한 회원 ID와 일치하는지 확인합니다.
     *
     * @param memberId 회원 ID
     * @return 일치 여부
     */
    boolean isCurrentUser(Long memberId);
}