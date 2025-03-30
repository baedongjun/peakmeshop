package com.peakmeshop.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.peakmeshop.entity.Wishlist;

public interface WishlistRepository extends JpaRepository<Wishlist, Long> {

    Optional<Wishlist> findByMemberId(Long memberId);

    Page<Wishlist> findByMemberId(Long memberId, Pageable pageable);

    Optional<Wishlist> findByMemberIdAndId(Long memberId, Long id);

    Optional<Wishlist> findByMemberIdAndIsDefaultTrue(Long memberId);

    @Query("SELECT w FROM Wishlist w WHERE w.isPublic = true")
    Page<Wishlist> findPublicWishlists(Pageable pageable);

    @Query("SELECT w FROM Wishlist w WHERE w.shareUrl = :shareUrl")
    Optional<Wishlist> findByShareUrl(@Param("shareUrl") String shareUrl);

    long countByMemberId(Long memberId);

    void deleteByMemberIdAndId(Long memberId, Long id);
}