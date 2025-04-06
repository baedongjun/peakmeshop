package com.peakmeshop.api.controller;

import com.peakmeshop.api.dto.PopupDTO;
import com.peakmeshop.domain.service.PopupService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/popups")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class PopupController {

    private final PopupService popupService;

    @GetMapping
    public ResponseEntity<Page<PopupDTO>> getAllPopups(@PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(popupService.getAllPopups(pageable));
    }

    @GetMapping("/active")
    public ResponseEntity<List<PopupDTO>> getActivePopups() {
        return ResponseEntity.ok(popupService.getActivePopups());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PopupDTO> getPopupById(@PathVariable Long id) {
        return ResponseEntity.ok(popupService.getPopupById(id));
    }

    @PostMapping
    public ResponseEntity<PopupDTO> createPopup(@RequestBody PopupDTO popupDTO) {
        return ResponseEntity.ok(popupService.createPopup(popupDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PopupDTO> updatePopup(@PathVariable Long id, @RequestBody PopupDTO popupDTO) {
        return ResponseEntity.ok(popupService.updatePopup(id, popupDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePopup(@PathVariable Long id) {
        popupService.deletePopup(id);
        return ResponseEntity.ok().build();
    }
} 