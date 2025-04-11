package com.peakmeshop.api.controller;

import com.peakmeshop.api.dto.AdminMenuDTO;
import com.peakmeshop.domain.service.AdminMenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/menus")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminMenuController {

    private final AdminMenuService adminMenuService;

    @GetMapping
    public ResponseEntity<List<AdminMenuDTO>> getAllMenus() {
        return ResponseEntity.ok(adminMenuService.getAllMenus());
    }

    @GetMapping("/root")
    public ResponseEntity<List<AdminMenuDTO>> getRootMenus() {
        return ResponseEntity.ok(adminMenuService.getRootMenus());
    }

    @GetMapping("/children/{parentId}")
    public ResponseEntity<List<AdminMenuDTO>> getChildMenus(@PathVariable Long parentId) {
        return ResponseEntity.ok(adminMenuService.getChildMenus(parentId));
    }

    @GetMapping("/active")
    public ResponseEntity<List<AdminMenuDTO>> getActiveMenus() {
        return ResponseEntity.ok(adminMenuService.getActiveMenus());
    }

    @PostMapping
    public ResponseEntity<AdminMenuDTO> createMenu(@RequestBody AdminMenuDTO menuDTO) {
        return ResponseEntity.ok(adminMenuService.createMenu(menuDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AdminMenuDTO> updateMenu(@PathVariable Long id, @RequestBody AdminMenuDTO menuDTO) {
        return ResponseEntity.ok(adminMenuService.updateMenu(id, menuDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMenu(@PathVariable Long id) {
        adminMenuService.deleteMenu(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/order")
    public ResponseEntity<List<AdminMenuDTO>> updateMenuOrder(@RequestBody List<AdminMenuDTO> menuDTOs) {
        return ResponseEntity.ok(adminMenuService.updateMenuOrder(menuDTOs));
    }
} 