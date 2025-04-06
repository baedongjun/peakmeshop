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
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PopupServiceImpl implements PopupService {

    private final PopupRepository popupRepository;

    @Override
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
    public PopupDTO getPopupById(Long id) {
        return popupRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new RuntimeException("Popup not found"));
    }

    @Override
    @Transactional
    public PopupDTO createPopup(PopupDTO popupDTO) {
        Popup popup = convertToEntity(popupDTO);
        popup = popupRepository.save(popup);
        return convertToDTO(popup);
    }

    @Override
    @Transactional
    public PopupDTO updatePopup(Long id, PopupDTO popupDTO) {
        Popup popup = popupRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Popup not found"));

        popup.setTitle(popupDTO.getTitle());
        popup.setContent(popupDTO.getContent());
        popup.setImageUrl(popupDTO.getImageUrl());
        popup.setLinkUrl(popupDTO.getLinkUrl());
        popup.setTarget(popupDTO.getTarget());
        popup.setWidth(popupDTO.getWidth());
        popup.setHeight(popupDTO.getHeight());
        popup.setPosition(popupDTO.getPosition());
        popup.setOrder(popupDTO.getOrder());
        popup.setStartDateTime(popupDTO.getStartDateTime());
        popup.setEndDateTime(popupDTO.getEndDateTime());
        popup.setIsActive(popupDTO.getIsActive());
        popup.setDeviceType(popupDTO.getDeviceType());
        popup.setShowTodayClose(popupDTO.getShowTodayClose());

        popup = popupRepository.save(popup);
        return convertToDTO(popup);
    }

    @Override
    @Transactional
    public void deletePopup(Long id) {
        popupRepository.deleteById(id);
    }

    private PopupDTO convertToDTO(Popup popup) {
        PopupDTO dto = new PopupDTO();
        dto.setId(popup.getId());
        dto.setTitle(popup.getTitle());
        dto.setContent(popup.getContent());
        dto.setImageUrl(popup.getImageUrl());
        dto.setLinkUrl(popup.getLinkUrl());
        dto.setTarget(popup.getTarget());
        dto.setWidth(popup.getWidth());
        dto.setHeight(popup.getHeight());
        dto.setPosition(popup.getPosition());
        dto.setOrder(popup.getOrder());
        dto.setStartDateTime(popup.getStartDateTime());
        dto.setEndDateTime(popup.getEndDateTime());
        dto.setIsActive(popup.getIsActive());
        dto.setDeviceType(popup.getDeviceType());
        dto.setShowTodayClose(popup.getShowTodayClose());
        return dto;
    }

    private Popup convertToEntity(PopupDTO dto) {
        Popup popup = new Popup();
        popup.setTitle(dto.getTitle());
        popup.setContent(dto.getContent());
        popup.setImageUrl(dto.getImageUrl());
        popup.setLinkUrl(dto.getLinkUrl());
        popup.setTarget(dto.getTarget());
        popup.setWidth(dto.getWidth());
        popup.setHeight(dto.getHeight());
        popup.setPosition(dto.getPosition());
        popup.setOrder(dto.getOrder());
        popup.setStartDateTime(dto.getStartDateTime());
        popup.setEndDateTime(dto.getEndDateTime());
        popup.setIsActive(dto.getIsActive());
        popup.setDeviceType(dto.getDeviceType());
        popup.setShowTodayClose(dto.getShowTodayClose());
        return popup;
    }
} 