package com.peakmeshop.domain.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.peakmeshop.domain.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(String email);

    Optional<Member> findByUserId(String userId);

    Optional<Member> findByVerificationToken(String token);

    Optional<Member> findByResetToken(String token);

    boolean existsByUserId(String userId);
    boolean existsByEmail(String email);

    List<Member> findByEmailContainingOrNameContaining(String email, String name);

    Page<Member> findByEmailContainingOrNameContaining(String email, String name, Pageable pageable);

    // 특정 기간 동안 가입한 회원 수 계산
    long countByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
}