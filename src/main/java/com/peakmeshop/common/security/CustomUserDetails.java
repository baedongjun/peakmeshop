package com.peakmeshop.common.security;

import com.peakmeshop.domain.entity.Member;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {

    private final Member member;
    private final Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetails(Member member) {
        this.member = member;
        this.authorities = createAuthorities(member);
    }

    private Collection<? extends GrantedAuthority> createAuthorities(Member member) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(member.getUserRole()));

        if ("ROLE_ADMIN".equals(member.getUserRole())) {
            authorities.add(new SimpleGrantedAuthority("PERMISSION_DELETE"));
            authorities.add(new SimpleGrantedAuthority("PERMISSION_UPDATE"));
            authorities.add(new SimpleGrantedAuthority("PERMISSION_CREATE"));
        }

        return authorities;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return member.getPassword();
    }

    @Override
    public String getUsername() {
        return member.getUserId();
    }

    @Override
    public boolean isAccountNonExpired() {
        // 계정 만료 로직 구현
        // 예: 1년 이상 로그인하지 않은 경우 만료로 처리
        if (member.getLastLoginAt() == null) {
            return false;
        }

        return !member.getLastLoginAt().plusYears(1).isBefore(LocalDateTime.now());
    }

    @Override
    public boolean isAccountNonLocked() {
        return !member.isWithdrawn() && !Member.STATUS_BLOCKED.equals(member.getStatus());
    }

    @Override
    public boolean isCredentialsNonExpired() {
        // 비밀번호 만료 로직 구현
        // 예: 90일마다 비밀번호 변경 필요
        if (member.getUpdatedAt() == null) {
            return false;
        }

        return !member.getUpdatedAt().plusDays(90).isBefore(LocalDateTime.now());
    }

    @Override
    public boolean isEnabled() {
        return member.isEnabled();
    }

    public Long getMemberId() {
        return member.getId();
    }

    public String getName() {
        return member.getName();
    }

    public String getEmail() {
        return member.getEmail();
    }
}
