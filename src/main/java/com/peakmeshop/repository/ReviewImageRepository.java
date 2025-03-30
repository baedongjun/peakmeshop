package com.peakmeshop.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.peakmeshop.entity.ReviewImage;

public interface ReviewImageRepository extends JpaRepository<ReviewImage, Long> {

    List<ReviewImage> findByReviewId(Long reviewId);

    void deleteByReviewId(Long reviewId);
}