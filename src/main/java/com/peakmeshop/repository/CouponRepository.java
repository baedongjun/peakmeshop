package com.peakmeshop.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.peakmeshop.entity.Coupon;

public interface CouponRepository extends JpaRepository<Coupon, Long> {

    Optional<Coupon> findByCode(String code);

    List<Coupon> findByStatus(String status);

    Page<Coupon> findByStatus(String status, Pageable pageable);

    List<Coupon> findByEndDateBeforeAndStatusNot(LocalDateTime date, String status);
}