package com.peakmeshop.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetailsService;

import com.peakmeshop.dto.MemberDTO;

public interface MemberService extends UserDetailsService {

    /**
     * 회원 가입
     * @param memberDTO 회원 정보
     * @return 저장된 회원 정보
     */
    MemberDTO register(MemberDTO memberDTO);

    /**
     * 이메일로 회원 조회
     * @param email 이메일
     * @return 회원 정보
     */
    Optional<MemberDTO> findByEmail(String email);

    /**
     * ID로 회원 조회
     * @param id 회원 ID
     * @return 회원 정보
     */
    Optional<MemberDTO> findById(Long id);

    /**
     * 회원 정보 업데이트
     * @param id 회원 ID
     * @param memberDTO 업데이트할 회원 정보
     * @return 업데이트된 회원 정보
     */
    MemberDTO updateMember(Long id, MemberDTO memberDTO);

    /**
     * 비밀번호 변경
     * @param id 회원 ID
     * @param currentPassword 현재 비밀번호
     * @param newPassword 새 비밀번호
     * @return 성공 여부
     */
    boolean changePassword(Long id, String currentPassword, String newPassword);

    /**
     * 회원 비활성화
     * @param id 회원 ID
     */
    void deactivateMember(Long id);

    /**
     * 회원 활성화
     * @param id 회원 ID
     */
    void activateMember(Long id);

    /**
     * 회원 삭제
     * @param id 회원 ID
     */
    void deleteMember(Long id);

    /**
     * 회원 검색
     * @param keyword 검색어
     * @param pageable 페이징 정보
     * @return 검색 결과
     */
    Page<MemberDTO> searchMembers(String keyword, Pageable pageable);

    /**
     * 이메일 중복 확인
     * @param email 이메일
     * @return 중복 여부
     */
    boolean isEmailDuplicated(String email);

    /**
     * 로그인 시간 업데이트
     * @param email 이메일
     */
    void updateLastLoginTime(String email);
}