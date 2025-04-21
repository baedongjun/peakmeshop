package com.peakmeshop.domain.repository;

import com.peakmeshop.domain.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {

    @Query("SELECT c FROM Cart c WHERE c.member.id = :memberId")
    Optional<Cart> findByMemberId(@Param("memberId") Long memberId);

    @Query("SELECT c FROM Cart c WHERE c.member.userId = :userId")
    Optional<Cart> findByUserId(@Param("userId") String userId);
}