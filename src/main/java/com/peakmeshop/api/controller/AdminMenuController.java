package com.peakmeshop.api.controller;

import com.peakmeshop.api.dto.MenuDTO;
import com.peakmeshop.domain.entity.Menu;
import com.peakmeshop.domain.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/menus")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminMenuController {

    private final MenuService menuService;

    @GetMapping
    public ResponseEntity<List<MenuDTO>> getMenus() {
        return ResponseEntity.ok(menuService.getMenus());
    }

    @PostMapping
    public ResponseEntity<MenuDTO> createMenu(@RequestBody MenuDTO menu) {
        return ResponseEntity.ok(menuService.createMenu(menu));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MenuDTO> updateMenu(@PathVariable Long id, @RequestBody MenuDTO menu) {
        return ResponseEntity.ok(menuService.updateMenu(id, menu));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMenu(@PathVariable Long id) {
        menuService.deleteMenu(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/order")
    public ResponseEntity<Void> changeMenuOrder(
            @PathVariable Long id,
            @RequestParam int newOrder) {
        menuService.changeMenuOrder(id, newOrder);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/toggle")
    public ResponseEntity<Void> toggleMenuStatus(@PathVariable Long id) {
        menuService.toggleMenuStatus(id);
        return ResponseEntity.ok().build();
    }
} 