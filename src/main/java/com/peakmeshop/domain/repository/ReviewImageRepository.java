package com.peakmeshop.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.peakmeshop.domain.entity.ReviewImage;

public interface ReviewImageRepository extends JpaRepository<ReviewImage, Long> {

    List<ReviewImage> findByReviewId(Long reviewId);

    void deleteByReviewId(Long reviewId);
}