package com.peakmeshop.repository;

import com.peakmeshop.entity.WishlistItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface WishlistItemRepository extends JpaRepository<WishlistItem, Long> {

    List<WishlistItem> findByWishlistId(Long wishlistId);

    Optional<WishlistItem> findByWishlistIdAndProductId(Long wishlistId, Long productId);

    @Query("SELECT wi FROM WishlistItem wi WHERE wi.wishlist.member.id = :memberId AND wi.product.id = :productId")
    List<WishlistItem> findByMemberIdAndProductId(@Param("memberId") Long memberId, @Param("productId") Long productId);

    @Query("SELECT COUNT(wi) FROM WishlistItem wi WHERE wi.product.id = :productId")
    long countByProductId(@Param("productId") Long productId);

    @Query("SELECT wi.product.id, COUNT(wi) as wishCount FROM WishlistItem wi GROUP BY wi.product.id ORDER BY wishCount DESC")
    List<Object[]> findMostWishedProducts(Pageable pageable);

    void deleteByWishlistId(Long wishlistId);

    void deleteByWishlistIdAndProductId(Long wishlistId, Long productId);

    @Query("SELECT wi FROM WishlistItem wi WHERE wi.isNotifyOnSale = true AND wi.product.id = :productId")
    List<WishlistItem> findItemsToNotifyOnSale(@Param("productId") Long productId);

    @Query("SELECT wi FROM WishlistItem wi WHERE wi.isNotifyOnStockAvailable = true AND wi.product.id = :productId")
    List<WishlistItem> findItemsToNotifyOnStockAvailable(@Param("productId") Long productId);

    // 회원 ID로 위시리스트 아이템 조회
    Page<WishlistItem> findByWishlistMemberId(Long memberId, Pageable pageable);
}