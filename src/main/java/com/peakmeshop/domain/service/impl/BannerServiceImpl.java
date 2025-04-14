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
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class BannerServiceImpl implements BannerService {

    private final BannerRepository bannerRepository;
    private final FileStorageService fileStorageService;

    @Override
    public Page<BannerDTO> getBanners(Pageable pageable) {
        return bannerRepository.findAll(pageable)
                .map(this::convertToDTO);
    }

    @Override
    public Page<BannerDTO> getActiveBanners(Pageable pageable) {
        LocalDateTime now = LocalDateTime.now();
        return bannerRepository.findByStartDateBeforeAndEndDateAfterAndIsActiveTrue(now, now, pageable)
                .map(this::convertToDTO);
    }

    @Override
    public List<BannerDTO> getActiveBannersByPosition(String position) {
        LocalDateTime now = LocalDateTime.now();
        return bannerRepository.findByPositionAndStartDateBeforeAndEndDateAfterAndIsActiveTrue(position, now, now)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public BannerDTO getBannerById(Long id) {
        return bannerRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new IllegalArgumentException("Banner not found with id: " + id));
    }

    @Override
    public BannerDTO createBanner(BannerDTO bannerDTO) {
        Banner banner = convertToEntity(bannerDTO);
        return convertToDTO(bannerRepository.save(banner));
    }

    @Override
    public BannerDTO updateBanner(Long id, BannerDTO bannerDTO) {
        Banner existingBanner = bannerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Banner not found with id: " + id));
        
        Banner updatedBanner = convertToEntity(bannerDTO);
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
        
        return convertToDTO(bannerRepository.save(existingBanner));
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

    private BannerDTO convertToDTO(Banner banner) {
        return new BannerDTO(
                banner.getId(),
                banner.getTitle(),
                banner.getSubtitle(),
                banner.getDescription(),
                banner.getImageUrl(),
                banner.getLinkUrl(),
                banner.getPosition(),
                banner.getStatus(),
                banner.getSortOrder(),
                banner.getStartDate(),
                banner.getEndDate(),
                banner.isActive(),
                banner.getBackgroundColor(),
                banner.getTextColor(),
                banner.getCreatedAt(),
                banner.getUpdatedAt()
        );
    }

    private Banner convertToEntity(BannerDTO bannerDTO) {
        Banner banner = new Banner();
        banner.setTitle(bannerDTO.title());
        banner.setSubtitle(bannerDTO.subtitle());
        banner.setDescription(bannerDTO.description());
        banner.setImageUrl(bannerDTO.imageUrl());
        banner.setLinkUrl(bannerDTO.linkUrl());
        banner.setPosition(bannerDTO.position());
        banner.setStatus(bannerDTO.status());
        banner.setSortOrder(bannerDTO.sortOrder());
        banner.setStartDate(bannerDTO.startDate());
        banner.setEndDate(bannerDTO.endDate());
        banner.setActive(bannerDTO.isActive());
        banner.setBackgroundColor(bannerDTO.backgroundColor());
        banner.setTextColor(bannerDTO.textColor());
        return banner;
    }
}