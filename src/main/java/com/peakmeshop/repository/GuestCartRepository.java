package com.peakmeshop.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.peakmeshop.entity.GuestCart;

public interface GuestCartRepository extends JpaRepository<GuestCart, Long> {

    Optional<GuestCart> findBySessionId(String sessionId);

    boolean existsBySessionId(String sessionId);
}