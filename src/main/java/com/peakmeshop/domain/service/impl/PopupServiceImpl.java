package com.peakmeshop.domain.service.impl;

import com.peakmeshop.api.dto.PopupDTO;
import com.peakmeshop.domain.entity.Popup;
import com.peakmeshop.domain.repository.PopupRepository;
import com.peakmeshop.domain.service.PopupService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class PopupServiceImpl implements PopupService {

    private final PopupRepository popupRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<PopupDTO> getAllPopups(Pageable pageable) {
        return popupRepository.findAll(pageable)
                .map(this::convertToDTO);
    }

    @Override
    public List<PopupDTO> getActivePopups() {
        return popupRepository.findActivePopups(LocalDateTime.now())
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PopupDTO getPopupById(Long id) {
        return popupRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new RuntimeException("Popup not found with id: " + id));
    }

    @Override
    public PopupDTO createPopup(PopupDTO popupDTO) {
        Popup popup = convertToEntity(popupDTO);
        return convertToDTO(popupRepository.save(popup));
    }

    @Override
    public PopupDTO updatePopup(Long id, PopupDTO popupDTO) {
        Popup existingPopup = popupRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Popup not found with id: " + id));

        updateEntityFromDTO(existingPopup, popupDTO);
        return convertToDTO(popupRepository.save(existingPopup));
    }

    @Override
    public void deletePopup(Long id) {
        popupRepository.deleteById(id);
    }

    @Override
    public void togglePopupStatus(Long id) {
        Popup popup = popupRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Popup not found with id: " + id));
        popup.setActive(!popup.isActive());
        popupRepository.save(popup);
    }

    private PopupDTO convertToDTO(Popup popup) {
        return PopupDTO.builder()
                .id(popup.getId())
                .title(popup.getTitle())
                .content(popup.getContent())
                .imageUrl(popup.getImageUrl())
                .linkUrl(popup.getLinkUrl())
                .sunser(popup.getSunser())
                .width(popup.getWidth())
                .height(popup.getHeight())
                .positionX(popup.getPositionX())
                .positionY(popup.getPositionY())
                .startDateTime(popup.getStartDateTime())
                .endDateTime(popup.getEndDateTime())
                .isActive(popup.isActive())
                .isNewWindow(popup.isNewWindow())
                .isScrollable(popup.isScrollable())
                .isResizable(popup.isResizable())
                .isDraggable(popup.isDraggable())
                .backgroundColor(popup.getBackgroundColor())
                .textColor(popup.getTextColor())
                .borderColor(popup.getBorderColor())
                .borderWidth(popup.getBorderWidth())
                .borderRadius(popup.getBorderRadius())
                .shadowColor(popup.getShadowColor())
                .shadowBlur(popup.getShadowBlur())
                .shadowSpread(popup.getShadowSpread())
                .shadowX(popup.getShadowX())
                .shadowY(popup.getShadowY())
                .createdAt(popup.getCreatedAt())
                .updatedAt(popup.getUpdatedAt())
                .build();
    }

    private Popup convertToEntity(PopupDTO dto) {
        Popup popup = new Popup();
        updateEntityFromDTO(popup, dto);
        return popup;
    }

    private void updateEntityFromDTO(Popup popup, PopupDTO dto) {
        popup.setTitle(dto.getTitle());
        popup.setContent(dto.getContent());
        popup.setImageUrl(dto.getImageUrl());
        popup.setLinkUrl(dto.getLinkUrl());
        popup.setSunser(dto.getSunser());
        popup.setWidth(dto.getWidth());
        popup.setHeight(dto.getHeight());
        popup.setPositionX(dto.getPositionX());
        popup.setPositionY(dto.getPositionY());
        popup.setStartDateTime(dto.getStartDateTime());
        popup.setEndDateTime(dto.getEndDateTime());
        popup.setActive(dto.isActive());
        popup.setNewWindow(dto.isNewWindow());
        popup.setScrollable(dto.isScrollable());
        popup.setResizable(dto.isResizable());
        popup.setDraggable(dto.isDraggable());
        popup.setBackgroundColor(dto.getBackgroundColor());
        popup.setTextColor(dto.getTextColor());
        popup.setBorderColor(dto.getBorderColor());
        popup.setBorderWidth(dto.getBorderWidth());
        popup.setBorderRadius(dto.getBorderRadius());
        popup.setShadowColor(dto.getShadowColor());
        popup.setShadowBlur(dto.getShadowBlur());
        popup.setShadowSpread(dto.getShadowSpread());
        popup.setShadowX(dto.getShadowX());
        popup.setShadowY(dto.getShadowY());
    }
} 