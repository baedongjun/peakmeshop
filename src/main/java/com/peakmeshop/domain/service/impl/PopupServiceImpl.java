package com.peakmeshop.domain.service.impl;

import com.peakmeshop.api.dto.PopupDTO;
import com.peakmeshop.api.mapper.PopupMapper;
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
    private final PopupMapper popupMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<PopupDTO> getAllPopups(Pageable pageable) {
        return popupRepository.findAll(pageable)
                .map(popupMapper::toDTO);
    }

    @Override
    public List<PopupDTO> getActivePopups() {
        return popupRepository.findActivePopups(LocalDateTime.now())
                .stream()
                .map(popupMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PopupDTO getPopupById(Long id) {
        return popupRepository.findById(id)
                .map(popupMapper::toDTO)
                .orElseThrow(() -> new RuntimeException("Popup not found with id: " + id));
    }

    @Override
    public PopupDTO createPopup(PopupDTO popupDTO) {
        Popup popup = popupMapper.toEntity(popupDTO);
        return popupMapper.toDTO(popupRepository.save(popup));
    }

    @Override
    public PopupDTO updatePopup(Long id, PopupDTO popupDTO) {
        Popup existingPopup = popupRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Popup not found with id: " + id));
        
        Popup updatedPopup = popupMapper.toEntity(popupDTO);
        updatedPopup.setId(id);
        return popupMapper.toDTO(popupRepository.save(updatedPopup));
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
} 