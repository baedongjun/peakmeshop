package com.peakmeshop.domain.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.peakmeshop.api.dto.BannerDTO;
import com.peakmeshop.domain.entity.Banner;
import com.peakmeshop.domain.repository.BannerRepository;
import com.peakmeshop.domain.service.BannerService;
import com.peakmeshop.domain.service.FileStorageService;
import com.peakmeshop.api.mapper.BannerMapper;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class BannerServiceImpl implements BannerService {

    private final BannerRepository bannerRepository;
    private final FileStorageService fileStorageService;
    private final BannerMapper bannerMapper;

    @Override
    public Page<BannerDTO> getBanners(Pageable pageable) {
        return bannerRepository.findAll(pageable)
                .map(bannerMapper::toDTO);
    }

    @Override
    public Page<BannerDTO> getActiveBanners(Pageable pageable) {
        LocalDateTime now = LocalDateTime.now();
        return bannerRepository.findByStartDateBeforeAndEndDateAfterAndIsActiveTrue(now, now, pageable)
                .map(bannerMapper::toDTO);
    }

    @Override
    public List<BannerDTO> getActiveBannersByPosition(String position) {
        LocalDateTime now = LocalDateTime.now();
        return bannerRepository.findByPositionAndStartDateBeforeAndEndDateAfterAndIsActiveTrue(position, now, now)
                .stream()
                .map(bannerMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public BannerDTO getBannerById(Long id) {
        return bannerRepository.findById(id)
                .map(bannerMapper::toDTO)
                .orElseThrow(() -> new IllegalArgumentException("Banner not found with id: " + id));
    }

    @Override
    public BannerDTO createBanner(BannerDTO bannerDTO) {
        Banner banner = bannerMapper.toEntity(bannerDTO);
        return bannerMapper.toDTO(bannerRepository.save(banner));
    }

    @Override
    public BannerDTO updateBanner(Long id, BannerDTO bannerDTO) {
        Banner existingBanner = bannerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Banner not found with id: " + id));
        
        Banner updatedBanner = bannerMapper.toEntity(bannerDTO);
        existingBanner.setTitle(updatedBanner.getTitle());
        existingBanner.setSubtitle(updatedBanner.getSubtitle());
        existingBanner.setDescription(updatedBanner.getDescription());
        existingBanner.setImageUrl(updatedBanner.getImageUrl());
        existingBanner.setLinkUrl(updatedBanner.getLinkUrl());
        existingBanner.setPosition(updatedBanner.getPosition());
        existingBanner.setStatus(updatedBanner.getStatus());
        existingBanner.setSortOrder(updatedBanner.getSortOrder());
        existingBanner.setStartDate(updatedBanner.getStartDate());
        existingBanner.setEndDate(updatedBanner.getEndDate());
        existingBanner.setActive(updatedBanner.isActive());
        existingBanner.setBackgroundColor(updatedBanner.getBackgroundColor());
        existingBanner.setTextColor(updatedBanner.getTextColor());
        
        return bannerMapper.toDTO(bannerRepository.save(existingBanner));
    }

    @Override
    public boolean deleteBanner(Long id) {
        try {
            Banner banner = bannerRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Banner not found with id: " + id));
            bannerRepository.delete(banner);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void changeBannerOrder(Long id, int newOrder) {
        Banner banner = bannerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Banner not found with id: " + id));
        
        int oldOrder = banner.getSortOrder();
        if (newOrder > oldOrder) {
            bannerRepository.decreaseOrderBetween(oldOrder + 1, newOrder);
        } else if (newOrder < oldOrder) {
            bannerRepository.increaseOrderBetween(newOrder, oldOrder - 1);
        }
        
        banner.setSortOrder(newOrder);
        bannerRepository.save(banner);
    }

    @Override
    public void toggleBannerStatus(Long id) {
        Banner banner = bannerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Banner not found with id: " + id));
        banner.setActive(!banner.isActive());
        bannerRepository.save(banner);
    }
}