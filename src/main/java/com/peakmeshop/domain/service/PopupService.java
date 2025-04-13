package com.peakmeshop.domain.service;

import com.peakmeshop.api.dto.PopupDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface PopupService {
    Page<PopupDTO> getAllPopups(Pageable pageable);
    List<PopupDTO> getActivePopups();
    PopupDTO getPopupById(Long id);
    PopupDTO createPopup(PopupDTO popupDTO);
    PopupDTO updatePopup(Long id, PopupDTO popupDTO);
    void deletePopup(Long id);
    void togglePopupStatus(Long id);
} 