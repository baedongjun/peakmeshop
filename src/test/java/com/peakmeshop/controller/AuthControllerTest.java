package com.peakmeshop.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.peakmeshop.dto.LoginRequest;
import com.peakmeshop.entity.Member;
import com.peakmeshop.repository.MemberRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final String TEST_EMAIL = "admin@peakmeshop.com";
    private static final String TEST_PASSWORD = "password";

    @BeforeEach
    public void setup() {
        // 기존 테스트 사용자가 있으면 삭제
        memberRepository.findByEmail(TEST_EMAIL).ifPresent(member ->
                memberRepository.delete(member)
        );

        // 테스트 사용자 생성
        Member member = new Member();
        member.setEmail(TEST_EMAIL);
        member.setPassword(passwordEncoder.encode(TEST_PASSWORD));
        member.setName("Test Admin");
        member.setUserRole("ROLE_ADMIN");
        // active 필드가 없는 경우 이 줄 제거
        // member.setActive(true);

        memberRepository.save(member);
    }

    @Test
    public void testLogin_Success() throws Exception {
        LoginRequest loginRequest = new LoginRequest(TEST_EMAIL, TEST_PASSWORD);
        String requestJson = objectMapper.writeValueAsString(loginRequest);

        mockMvc.perform(
                        post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.member").exists());
    }

    @Test
    public void testLogin_Failure() throws Exception {
        LoginRequest loginRequest = new LoginRequest(TEST_EMAIL, "wrongpassword");
        String requestJson = objectMapper.writeValueAsString(loginRequest);

        mockMvc.perform(
                        post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                )
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = TEST_EMAIL, roles = {"ADMIN"})
    public void testGetCurrentUser_Success() throws Exception {
        mockMvc.perform(get("/api/members/me"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(TEST_EMAIL));
    }
}