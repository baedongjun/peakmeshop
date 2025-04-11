package com.peakmeshop.common.security;

import java.security.Principal;

import org.springframework.stereotype.Component;

import com.peakmeshop.domain.repository.MemberRepository;

@Component
public class MemberSecurity {

    private final MemberRepository memberRepository;

    public MemberSecurity(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    /**
     * 현재 로그인한 사용자가 요청한 회원 ID와 일치하는지 확인
     * @param memberId 회원 ID
     * @param principal 현재 로그인한 사용자
     * @return 일치 여부
     */
    public boolean isCurrentMember(Long memberId, Principal principal) {
        if (principal == null) {
            return false;
        }

        return memberRepository.findByUserId(principal.getName())
                .map(member -> member.getId().equals(memberId))
                .orElse(false);
    }
}