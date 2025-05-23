package com.peakmeshop.domain.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.peakmeshop.domain.entity.GuestCartItem;

public interface GuestCartItemRepository extends JpaRepository<GuestCartItem, Long> {

    Optional<GuestCartItem> findByGuestCartIdAndProductId(Long guestCartId, Long productId);

    Optional<GuestCartItem> findByGuestCartIdAndProductIdAndOptionId(Long guestCartId, Long productId, Long optionId);

    void deleteByGuestCartId(Long guestCartId);
}