package com.peakmeshop.common.security;

import com.peakmeshop.domain.entity.Member;
import com.peakmeshop.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        log.info("loadUserByUsername 호출됨 - userId: {}", userId);

        try {
            // username 파라미터를 userId로 사용하여 회원 조회
            Member member = memberRepository.findByUserId(userId)
                    .orElseThrow(() -> {
                        log.error("사용자를 찾을 수 없음: {}", userId);
                        return new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + userId);
                    });

            log.info("회원 조회 성공: {}", member.getUserId());

            // UserDetails 객체 생성 및 반환
            return new CustomUserDetails(member);
        } catch (Exception e) {
            log.error("loadUserByUsername 처리 중 오류 발생", e);
            throw e;
        }
    }


}