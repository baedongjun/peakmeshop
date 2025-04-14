package com.peakmeshop.domain.service;

import com.peakmeshop.domain.entity.Banner;
import com.peakmeshop.api.dto.BannerDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BannerService {
    
    // 배너 목록 조회
    Page<BannerDTO> getBanners(Pageable pageable);
    
    // 활성 배너 조회
    Page<BannerDTO> getActiveBanners(Pageable pageable);
    
    // 위치별 활성 배너 조회
    List<BannerDTO> getActiveBannersByPosition(String position);
    
    // 배너 상세 조회
    BannerDTO getBannerById(Long id);
    
    // 배너 생성
    BannerDTO createBanner(BannerDTO bannerDTO);
    
    // 배너 수정
    BannerDTO updateBanner(Long id, BannerDTO bannerDTO);
    
    // 배너 삭제
    boolean deleteBanner(Long id);
    
    // 배너 순서 변경
    void changeBannerOrder(Long id, int newOrder);
    
    // 배너 상태 변경
    void toggleBannerStatus(Long id);
}