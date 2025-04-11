package com.peakmeshop.common.security;

import com.peakmeshop.domain.entity.Member;
import com.peakmeshop.domain.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String userid) throws UsernameNotFoundException {

        // username 파라미터를 userId로 사용하여 회원 조회
        Member member = memberRepository.findByUserId(userid)
                .orElseThrow(() -> {
                    return new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + userid);
                });

        // 사용자 권한 설정
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(member.getUserRole()));

        // 추가 권한이 필요한 경우 여기에 추가
        if ("ROLE_ADMIN".equals(member.getUserRole())) {
            authorities.add(new SimpleGrantedAuthority("PERMISSION_DELETE"));
            authorities.add(new SimpleGrantedAuthority("PERMISSION_UPDATE"));
            authorities.add(new SimpleGrantedAuthority("PERMISSION_CREATE"));
        }

        // 마지막 로그인 시간 업데이트
        updateLastLoginTime(member);

        // UserDetails 객체 생성 및 반환
        return new CustomUserDetails(
                member.getUserId(),
                member.getPassword(),
                member.isEnabled(),
                !isAccountExpired(member),
                !isCredentialsExpired(member),
                !isAccountLocked(member),
                authorities,
                member.getId(),
                member.getName(),
                member.getEmail()
        );
    }

    /**
     * 마지막 로그인 시간을 업데이트합니다.
     */
    @Transactional
    protected void updateLastLoginTime(Member member) {
        member.setLastLoginAt(LocalDateTime.now());
        memberRepository.save(member);
    }

    /**
     * 계정이 만료되었는지 확인합니다.
     */
    private boolean isAccountExpired(Member member) {
        // 계정 만료 로직 구현
        // 예: 1년 이상 로그인하지 않은 경우 만료로 처리
        if (member.getLastLoginAt() == null) {
            return false;
        }

        return member.getLastLoginAt().plusYears(1).isBefore(LocalDateTime.now());
    }

    /**
     * 자격 증명(비밀번호)이 만료되었는지 확인합니다.
     */
    private boolean isCredentialsExpired(Member member) {
        // 비밀번호 만료 로직 구현
        // 예: 90일마다 비밀번호 변경 필요
        if (member.getUpdatedAt() == null) {
            return false;
        }

        return member.getUpdatedAt().plusDays(90).isBefore(LocalDateTime.now());
    }

    /**
     * 계정이 잠겼는지 확인합니다.
     */
    private boolean isAccountLocked(Member member) {
        // 계정 잠금 상태 확인
        return Member.STATUS_BLOCKED.equals(member.getStatus()) || member.isWithdrawn();
    }

    /**
     * 커스텀 UserDetails 구현 클래스
     */
    public static class CustomUserDetails extends User {
        private final Long memberId;
        private final String name;
        private final String email;

        public CustomUserDetails(String username, String password, boolean enabled,
                                 boolean accountNonExpired, boolean credentialsNonExpired,
                                 boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities,
                                 Long memberId, String name, String email) {
            super(username, password, enabled, accountNonExpired, credentialsNonExpired,
                    accountNonLocked, authorities);
            this.memberId = memberId;
            this.name = name;
            this.email = email;
        }

        public Long getMemberId() {
            return memberId;
        }

        public String getName() {
            return name;
        }

        public String getEmail() {
            return email;
        }
    }
}