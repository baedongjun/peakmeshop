package com.peakmeshop.domain.service.impl;

import com.peakmeshop.common.security.oauth2.user.UserPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.peakmeshop.domain.entity.Member;
import com.peakmeshop.domain.repository.MemberRepository;
import com.peakmeshop.domain.service.SecurityService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SecurityServiceImpl implements SecurityService {

    private final MemberRepository memberRepository;

    @Override
    public boolean isCurrentUser(Long memberId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() ||
                !(authentication.getPrincipal() instanceof UserPrincipal)) {
            return false;
        }

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Member member = memberRepository.findByUserId(userPrincipal.getUserId()).orElse(null);

        return member != null && member.getId().equals(memberId);
    }
}