package com.peakmeshop.domain.repository;

import com.peakmeshop.domain.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {
    /**
     * 멤버 ID로 관리자를 찾습니다.
     * @param memberId 멤버 ID
     * @return 관리자 정보
     */
    Optional<Admin> findByMemberId(Long memberId);

    /**
     * 멤버 사용자 ID로 관리자를 찾습니다.
     * @param userId 사용자 ID
     * @return 관리자 정보
     */
    @Query("SELECT a FROM Admin a JOIN a.member m WHERE m.userId = :userId")
    Optional<Admin> findByMemberUserId(@Param("userId") String userId);

    /**
     * 멤버 사용자 ID와 비밀번호로 관리자를 찾습니다.
     * @param userId 사용자 ID
     * @param password 비밀번호
     * @return 관리자 정보
     */
    @Query("SELECT a FROM Admin a JOIN a.member m WHERE m.userId = :userId AND m.password = :password")
    Optional<Admin> findByMemberUserIdAndPassword(@Param("userId") String userId, @Param("password") String password);

    /**
     * 관리자와 멤버 정보를 함께 조회합니다.
     * @param adminId 관리자 ID
     * @return 관리자 정보 (멤버 정보 포함)
     */
    @Query("SELECT a FROM Admin a LEFT JOIN FETCH a.member WHERE a.id = :adminId")
    Optional<Admin> findByIdWithMember(@Param("adminId") Long adminId);

    /**
     * 모든 관리자와 멤버 정보를 함께 조회합니다.
     * @return 관리자 목록 (멤버 정보 포함)
     */
    @Query("SELECT a FROM Admin a LEFT JOIN FETCH a.member")
    List<Admin> findAllWithMember();

    /**
     * 특정 직책을 가진 관리자를 조회합니다.
     * @param position 직책
     * @return 관리자 목록
     */
    List<Admin> findByPosition(String position);

    /**
     * 특정 언어를 사용하는 관리자를 조회합니다.
     * @param language 언어
     * @return 관리자 목록
     */
    List<Admin> findByLanguage(String language);

    /**
     * 2단계 인증이 활성화된 관리자를 조회합니다.
     * @return 관리자 목록
     */
    List<Admin> findByTwoFactorEnabledTrue();

    /**
     * 멤버 이름으로 관리자를 검색합니다.
     * @param name 이름
     * @return 관리자 목록
     */
    @Query("SELECT a FROM Admin a JOIN a.member m WHERE m.name LIKE %:name%")
    List<Admin> findByMemberNameContaining(@Param("name") String name);

    /**
     * 멤버 사용자 ID로 관리자를 검색합니다.
     * @param userId 사용자 ID
     * @return 관리자 목록
     */
    @Query("SELECT a FROM Admin a JOIN a.member m WHERE m.userId LIKE %:userId%")
    List<Admin> findByMemberUserIdContaining(@Param("userId") String userId);

    /**
     * 멤버 이메일로 관리자를 검색합니다.
     * @param email 이메일
     * @return 관리자 목록
     */
    @Query("SELECT a FROM Admin a JOIN a.member m WHERE m.email LIKE %:email%")
    List<Admin> findByMemberEmailContaining(@Param("email") String email);

    /**
     * 멤버 전화번호로 관리자를 검색합니다.
     * @param phone 전화번호
     * @return 관리자 목록
     */
    @Query("SELECT a FROM Admin a JOIN a.member m WHERE m.phone LIKE %:phone%")
    List<Admin> findByMemberPhoneContaining(@Param("phone") String phone);
}