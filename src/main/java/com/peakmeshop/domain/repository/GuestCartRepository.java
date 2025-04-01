package com.peakmeshop.domain.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.peakmeshop.domain.entity.GuestCart;

public interface GuestCartRepository extends JpaRepository<GuestCart, Long> {

    Optional<GuestCart> findBySessionId(String sessionId);

    boolean existsBySessionId(String sessionId);
}