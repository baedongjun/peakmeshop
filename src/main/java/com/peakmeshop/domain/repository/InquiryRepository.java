package com.peakmeshop.domain.repository;

import com.peakmeshop.domain.entity.Inquiry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InquiryRepository extends JpaRepository<Inquiry, Long> {
    
    Page<Inquiry> findByCategory(String category, Pageable pageable);
    
    Page<Inquiry> findByStatus(Inquiry.InquiryStatus status, Pageable pageable);
    
    Page<Inquiry> findByCategoryAndStatus(String category, Inquiry.InquiryStatus status, Pageable pageable);

    Page<Inquiry> findByMemberIdOrderByCreatedAtDesc(Long memberId, Pageable pageable);

    @Query("SELECT i FROM Inquiry i WHERE i.member.id = :memberId AND i.status = :status ORDER BY i.createdAt DESC")
    Page<Inquiry> findByMemberIdAndStatus(@Param("memberId") Long memberId, @Param("status") String status, Pageable pageable);

    @Query("SELECT i FROM Inquiry i WHERE i.member.id = :memberId AND i.product.id = :productId ORDER BY i.createdAt DESC")
    List<Inquiry> findByMemberIdAndProductId(@Param("memberId") Long memberId, @Param("productId") Long productId);

    @Query("SELECT COUNT(i) FROM Inquiry i WHERE i.member.id = :memberId AND i.status = :status")
    long countByMemberIdAndStatus(@Param("memberId") Long memberId, @Param("status") String status);

    @Query("SELECT DISTINCT i.type FROM Inquiry i")
    List<String> findAllInquiryTypes();

    @Query("SELECT i FROM Inquiry i WHERE i.member.id = :memberId AND i.isSecret = false ORDER BY i.createdAt DESC")
    Page<Inquiry> findPublicInquiriesByMemberId(@Param("memberId") Long memberId, Pageable pageable);

    @Query("SELECT i FROM Inquiry i WHERE i.product.id = :productId AND i.isSecret = false ORDER BY i.createdAt DESC")
    Page<Inquiry> findPublicInquiriesByProductId(@Param("productId") Long productId, Pageable pageable);
} 