package com.peakmeshop.security.oauth2.user;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import com.peakmeshop.entity.Member;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserPrincipal implements OAuth2User, UserDetails {

    private Long id;
    private String userId;  // userId 필드 추가
    private String email;
    private String password;
    private String name;
    private Collection<? extends GrantedAuthority> authorities;
    private Map<String, Object> attributes;

    public UserPrincipal(Long id, String userId, String email, String password, String name, Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.userId = userId;  // 생성자에 userId 추가
        this.email = email;
        this.password = password;
        this.name = name;
        this.authorities = authorities;
    }

    public static UserPrincipal create(Member member) {
        List<GrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority(member.getUserRole()));

        return new UserPrincipal(
                member.getId(),
                member.getUserId(),  // userId 추가
                member.getEmail(),
                member.getPassword(),
                member.getName(),
                authorities
        );
    }

    public static UserPrincipal create(Member member, Map<String, Object> attributes) {
        UserPrincipal userPrincipal = UserPrincipal.create(member);
        userPrincipal.setAttributes(attributes);
        return userPrincipal;
    }

    public List<String> getRoles() {
        return authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
    }

    public boolean isAdmin() {
        return authorities.stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }
}