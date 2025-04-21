package com.peakmeshop.domain.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.peakmeshop.domain.entity.Wishlist;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, Long> {

    Optional<Wishlist> findByMemberId(Long memberId);

    Page<Wishlist> findByMemberId(Long memberId, Pageable pageable);

    Optional<Wishlist> findByMemberIdAndId(Long memberId, Long id);

    Optional<Wishlist> findByMemberIdAndIsDefaultTrue(Long memberId);

    void deleteByMemberIdAndId(Long memberId, Long id);

    Page<Wishlist> findByMemberIdOrderByCreatedAtDesc(Long memberId, Pageable pageable);
    
    @Query("SELECT COUNT(w) FROM Wishlist w WHERE w.member.id = :memberId")
    long countByMemberId(@Param("memberId") Long memberId);
    
    boolean existsByMemberIdAndProductId(Long memberId, Long productId);
    
    void deleteByMemberIdAndProductId(Long memberId, Long productId);
}