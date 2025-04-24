package com.peakmeshop.domain.service.impl;

import com.peakmeshop.common.security.oauth2.user.UserPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.peakmeshop.domain.entity.Member;
import com.peakmeshop.domain.repository.MemberRepository;
import com.peakmeshop.domain.service.SecurityService;
import com.peakmeshop.api.mapper.MemberMapper;
import com.peakmeshop.api.dto.MemberDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class SecurityServiceImpl implements SecurityService {

    private final MemberRepository memberRepository;
    private final MemberMapper memberMapper;

    @Override
    @Transactional(readOnly = true)
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

    @Override
    @Transactional(readOnly = true)
    public MemberDTO getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() ||
                !(authentication.getPrincipal() instanceof UserPrincipal)) {
            return null;
        }

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Member member = memberRepository.findByUserId(userPrincipal.getUserId()).orElse(null);

        return member != null ? memberMapper.toDTO(member) : null;
    }
}