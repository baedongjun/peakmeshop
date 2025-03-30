package com.peakmeshop.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.peakmeshop.entity.Point;

public interface PointRepository extends JpaRepository<Point, Long> {

    Optional<Point> findByMemberId(Long memberId);
}