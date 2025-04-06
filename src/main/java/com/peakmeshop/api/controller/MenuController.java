package com.peakmeshop.api.controller;

import com.peakmeshop.api.dto.MenuDTO;
import com.peakmeshop.domain.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/menus")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    @GetMapping
    public ResponseEntity<List<MenuDTO>> getMenusByType(@RequestParam String type) {
        return ResponseEntity.ok(menuService.getMenusByType(type));
    }

    @PostMapping
    public ResponseEntity<MenuDTO> createMenu(@RequestBody MenuDTO menuDTO) {
        return ResponseEntity.ok(menuService.createMenu(menuDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MenuDTO> updateMenu(@PathVariable Long id, @RequestBody MenuDTO menuDTO) {
        return ResponseEntity.ok(menuService.updateMenu(id, menuDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMenu(@PathVariable Long id) {
        menuService.deleteMenu(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/order")
    public ResponseEntity<List<MenuDTO>> updateMenuOrder(@RequestBody List<MenuDTO> menuDTOs) {
        return ResponseEntity.ok(menuService.updateMenuOrder(menuDTOs));
    }
} 