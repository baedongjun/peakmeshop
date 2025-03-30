package com.peakmeshop.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.peakmeshop.entity.GuestCartItem;

public interface GuestCartItemRepository extends JpaRepository<GuestCartItem, Long> {

    Optional<GuestCartItem> findByGuestCartIdAndProductId(Long guestCartId, Long productId);

    Optional<GuestCartItem> findByGuestCartIdAndProductIdAndVariantId(Long guestCartId, Long productId, Long variantId);

    void deleteByGuestCartId(Long guestCartId);
}