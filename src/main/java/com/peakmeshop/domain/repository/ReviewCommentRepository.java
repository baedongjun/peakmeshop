package com.peakmeshop.domain.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.peakmeshop.domain.entity.ReviewComment;

public interface ReviewCommentRepository extends JpaRepository<ReviewComment, Long> {

    List<ReviewComment> findByReviewId(Long reviewId);

    Page<ReviewComment> findByReviewId(Long reviewId, Pageable pageable);

    @Query("SELECT rc FROM ReviewComment rc WHERE rc.review.id = :reviewId AND rc.isHidden = false")
    Page<ReviewComment> findVisibleCommentsByReviewId(@Param("reviewId") Long reviewId, Pageable pageable);

    void deleteByReviewId(Long reviewId);
}