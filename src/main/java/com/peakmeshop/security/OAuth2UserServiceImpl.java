package com.peakmeshop.security;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.peakmeshop.entity.Member;
import com.peakmeshop.repository.MemberRepository;
import com.peakmeshop.security.oauth2.user.OAuth2UserInfo;
import com.peakmeshop.security.oauth2.user.OAuth2UserInfoFactory;
import com.peakmeshop.security.oauth2.user.UserPrincipal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class OAuth2UserServiceImpl extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);

        try {
            return processOAuth2User(oAuth2UserRequest, oAuth2User);
        } catch (AuthenticationException ex) {
            throw ex;
        } catch (Exception ex) {
            // Throwing an instance of AuthenticationException will trigger the OAuth2AuthenticationFailureHandler
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest oAuth2UserRequest, OAuth2User oAuth2User) {
        String registrationId = oAuth2UserRequest.getClientRegistration().getRegistrationId();
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(registrationId, oAuth2User.getAttributes());

        if (!StringUtils.hasText(oAuth2UserInfo.getEmail())) {
            throw new OAuth2AuthenticationException("이메일을 찾을 수 없습니다.");
        }

        Optional<Member> memberOptional = memberRepository.findByEmail(oAuth2UserInfo.getEmail());
        Member member;

        if (memberOptional.isPresent()) {
            member = memberOptional.get();

            // 다른 OAuth2 제공자로 가입한 경우 예외 발생
            if (!registrationId.equals(member.getProvider())) {
                throw new OAuth2AuthenticationException(
                        "이미 " + member.getProvider() + " 계정으로 가입되어 있습니다. " +
                                "해당 계정으로 로그인해주세요.");
            }

            member = updateExistingUser(member, oAuth2UserInfo);
        } else {
            member = registerNewUser(oAuth2UserRequest, oAuth2UserInfo);
        }

        // 마지막 로그인 시간 업데이트
        member.setLastLoginAt(LocalDateTime.now());
        memberRepository.save(member);

        return UserPrincipal.create(member, oAuth2User.getAttributes());
    }

    private Member registerNewUser(OAuth2UserRequest oAuth2UserRequest, OAuth2UserInfo oAuth2UserInfo) {
        String registrationId = oAuth2UserRequest.getClientRegistration().getRegistrationId();

        Member member = Member.builder()
                .email(oAuth2UserInfo.getEmail())
                .name(oAuth2UserInfo.getName())
                .provider(registrationId)
                .providerId(oAuth2UserInfo.getId())
                .imageUrl(oAuth2UserInfo.getImageUrl())
                .userRole("ROLE_USER")
                .enabled(true) // active 대신 enabled 사용
                .status(Member.STATUS_ACTIVE)
                .emailVerified(true)
                .password("") // OAuth2 사용자는 비밀번호가 필요 없음
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return memberRepository.save(member);
    }

    private Member updateExistingUser(Member existingMember, OAuth2UserInfo oAuth2UserInfo) {
        existingMember.setName(oAuth2UserInfo.getName());
        existingMember.setImageUrl(oAuth2UserInfo.getImageUrl());
        existingMember.setUpdatedAt(LocalDateTime.now());

        return memberRepository.save(existingMember);
    }
}