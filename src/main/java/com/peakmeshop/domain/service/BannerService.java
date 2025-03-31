package com.peakmeshop.domain.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.peakmeshop.api.dto.BannerDTO;

public interface BannerService {

    Page<BannerDTO> getAllBanners(Pageable pageable);

    Page<BannerDTO> getActiveBanners(Pageable pageable);

    Optional<BannerDTO> getBannerById(Long id);

    BannerDTO createBanner(BannerDTO bannerDTO);

    BannerDTO updateBanner(Long id, BannerDTO bannerDTO);

    boolean deleteBanner(Long id);

    List<BannerDTO> getActiveBannersByPosition(String position);
}