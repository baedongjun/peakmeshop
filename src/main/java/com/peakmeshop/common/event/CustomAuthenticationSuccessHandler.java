package com.peakmeshop.common.event;

import com.peakmeshop.api.dto.MemberDTO;
import com.peakmeshop.domain.entity.Member;
import com.peakmeshop.domain.service.MemberService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final MemberService memberService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("인증 성공 핸들러 호출됨");
        
        // 1. 사용자 정보 가져오기
        String userId = authentication.getName();
        log.info("로그인 사용자 ID: {}", userId);
        
        MemberDTO member = memberService.getMemberByUserId(userId);
        log.info("사용자 정보 조회 완료: {}", member.getName());

        // 2. 마지막 로그인 시간 업데이트
        member.setLastLoginAt(LocalDateTime.now());
        memberService.updateMember(member.getId(), member);
        log.info("마지막 로그인 시간 업데이트 완료");

        // 3. 세션에 사용자 정보 저장
        HttpSession session = request.getSession();
        session.setAttribute("memberId", member.getId());
        session.setAttribute("memberName", member.getName());
        session.setAttribute("memberRole", member.getUserRole());
        log.info("세션 정보 저장 완료");

        // 4. 이전 페이지 URL 확인
        String targetUrl = (String) session.getAttribute("targetUrl");
        if (targetUrl != null && !targetUrl.isEmpty()) {
            session.removeAttribute("targetUrl");
            log.info("이전 페이지로 리다이렉트: {}", targetUrl);
            response.sendRedirect(targetUrl);
        } else {
            // 5. 기본 리다이렉트
            log.info("메인 페이지로 리다이렉트");
            response.sendRedirect("/");
        }
    }
}