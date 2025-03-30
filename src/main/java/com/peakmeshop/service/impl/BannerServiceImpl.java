package com.peakmeshop.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.peakmeshop.dto.BannerDTO;
import com.peakmeshop.entity.Banner;
import com.peakmeshop.exception.ResourceNotFoundException;
import com.peakmeshop.repository.BannerRepository;
import com.peakmeshop.service.BannerService;
import com.peakmeshop.service.FileStorageService;

@Service
public class BannerServiceImpl implements BannerService {

    private final BannerRepository bannerRepository;
    private final FileStorageService fileStorageService;

    public BannerServiceImpl(
            BannerRepository bannerRepository,
            FileStorageService fileStorageService) {
        this.bannerRepository = bannerRepository;
        this.fileStorageService = fileStorageService;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BannerDTO> getAllBanners(Pageable pageable) {
        return bannerRepository.findAll(pageable)
                .map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BannerDTO> getActiveBanners(Pageable pageable) {
        LocalDateTime now = LocalDateTime.now();
        return bannerRepository.findByStartDateBeforeAndEndDateAfterAndActiveTrue(now, now, pageable)
                .map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<BannerDTO> getBannerById(Long id) {
        return bannerRepository.findById(id)
                .map(this::convertToDTO);
    }

    @Override
    @Transactional
    public BannerDTO createBanner(BannerDTO bannerDTO) {
        Banner banner = new Banner();
        banner.setTitle(bannerDTO.title());
        banner.setSubtitle(bannerDTO.subtitle());
        banner.setImageUrl(bannerDTO.imageUrl());
        banner.setLinkUrl(bannerDTO.linkUrl());
        banner.setPosition(bannerDTO.position());
        banner.setStartDate(bannerDTO.startDate());
        banner.setEndDate(bannerDTO.endDate());
        banner.setActive(bannerDTO.isActive());
        banner.setCreatedAt(LocalDateTime.now());
        banner.setUpdatedAt(LocalDateTime.now());

        Banner savedBanner = bannerRepository.save(banner);
        return convertToDTO(savedBanner);
    }

    @Override
    @Transactional
    public BannerDTO updateBanner(Long id, BannerDTO bannerDTO) {
        Banner existingBanner = bannerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("배너를 찾을 수 없습니다. ID: " + id));

        existingBanner.setTitle(bannerDTO.title());
        existingBanner.setSubtitle(bannerDTO.subtitle());
        existingBanner.setImageUrl(bannerDTO.imageUrl());
        existingBanner.setLinkUrl(bannerDTO.linkUrl());
        existingBanner.setPosition(bannerDTO.position());
        existingBanner.setStartDate(bannerDTO.startDate());
        existingBanner.setEndDate(bannerDTO.endDate());
        existingBanner.setActive(bannerDTO.isActive());
        existingBanner.setUpdatedAt(LocalDateTime.now());

        Banner updatedBanner = bannerRepository.save(existingBanner);
        return convertToDTO(updatedBanner);
    }

    @Override
    @Transactional
    public boolean deleteBanner(Long id) {
        Banner banner = bannerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("배너를 찾을 수 없습니다. ID: " + id));

        // 이미지 파일 삭제
        if (banner.getImageUrl() != null && !banner.getImageUrl().isEmpty()) {
            String fileName = banner.getImageUrl().substring(banner.getImageUrl().lastIndexOf("/") + 1);
            fileStorageService.deleteFile(fileName);
        }

        bannerRepository.delete(banner);
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public List<BannerDTO> getActiveBannersByPosition(String position) {
        LocalDateTime now = LocalDateTime.now();
        return bannerRepository.findByPositionAndStartDateBeforeAndEndDateAfterAndActiveTrue(
                        position, now, now)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // 엔티티를 DTO로 변환
    private BannerDTO convertToDTO(Banner banner) {
        return new BannerDTO(
                banner.getId(),
                banner.getTitle(),
                banner.getSubtitle(),
                banner.getImageUrl(),
                banner.getLinkUrl(),
                banner.getPosition(),
                banner.getStartDate(),
                banner.getEndDate(),
                banner.isActive(),
                banner.getCreatedAt(),
                banner.getUpdatedAt()
        );
    }
}