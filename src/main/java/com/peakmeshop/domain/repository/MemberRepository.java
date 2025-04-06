package com.peakmeshop.domain.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.peakmeshop.domain.entity.Member;

@Repository
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

    Page<Member> findByIsWithdrawnTrue(Pageable pageable);
    
    Page<Member> findByIsWithdrawnTrueAndWithdrawnAtBetween(
            LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
            
    Page<Member> findByLastLoginAtBeforeAndIsWithdrawnFalse(LocalDateTime date, Pageable pageable);
}