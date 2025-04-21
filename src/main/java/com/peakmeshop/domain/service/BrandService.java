package com.peakmeshop.domain.service;

import java.util.List;
import java.util.Optional;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.peakmeshop.api.dto.BrandDTO;
import com.peakmeshop.api.dto.BrandNewsDTO;
import com.peakmeshop.api.dto.CategoryDTO;
import com.peakmeshop.domain.entity.Brand;
import com.peakmeshop.domain.entity.BrandNews;
import com.peakmeshop.domain.entity.Member;
import com.peakmeshop.domain.repository.BrandNewsRepository;
import com.peakmeshop.domain.repository.BrandRepository;
import com.peakmeshop.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

public interface BrandService {

    /**
     * 브랜드 목록 조회
     */
    Page<BrandDTO> getBrands(String category, String keyword, Pageable pageable);

    /**
     * 브랜드 카테고리 목록 조회
     */
    List<CategoryDTO> getBrandCategories();

    /**
     * 브랜드 조회수 증가
     */
    void incrementViewCount(Long id);

    /**
     * 브랜드 팔로우 여부 확인
     */
    boolean isFollowing(String username, Long brandId);

    /**
     * 브랜드 뉴스 목록 조회
     */
    Page<BrandNewsDTO> getBrandNews(Long brandId, Pageable pageable);

    /**
     * 브랜드 뉴스 상세 조회
     */
    BrandNewsDTO getBrandNewsById(Long newsId);

    /**
     * 브랜드 뉴스 조회수 증가
     */
    void incrementNewsViewCount(Long newsId);

    /**
     * 이전 브랜드 뉴스 조회
     */
    BrandNewsDTO getPrevBrandNews(Long brandId, Long newsId);

    /**
     * 다음 브랜드 뉴스 조회
     */
    BrandNewsDTO getNextBrandNews(Long brandId, Long newsId);

    /**
     * 브랜드 상세 조회
     */
    BrandDTO getBrandById(Long id);
}

