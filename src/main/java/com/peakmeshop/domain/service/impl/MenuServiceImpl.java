package com.peakmeshop.domain.service.impl;

import com.peakmeshop.domain.service.MenuService;
import com.peakmeshop.domain.entity.Menu;
import com.peakmeshop.domain.repository.MenuRepository;
import com.peakmeshop.api.dto.MenuDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class MenuServiceImpl implements MenuService {

    private final MenuRepository menuRepository;

    @Override
    public List<MenuDTO> getMenus() {
        return menuRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MenuDTO> getMenusByType(String type) {
        return menuRepository.findAllByType(type)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<MenuDTO> updateMenuOrder(List<MenuDTO> menuDTOs) {
        for (int i = 0; i < menuDTOs.size(); i++) {
            MenuDTO menuDTO = menuDTOs.get(i);
            Menu menu = menuRepository.findById(menuDTO.id())
                    .orElseThrow(() -> new RuntimeException("메뉴를 찾을 수 없습니다: " + menuDTO.id()));
            
            menu.updateSortOrder(i + 1);
            menuRepository.save(menu);
        }
        
        return menuDTOs;
    }

    @Override
    public MenuDTO getMenuById(Long id) {
        return menuRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new IllegalArgumentException("Menu not found with id: " + id));
    }

    @Override
    public MenuDTO createMenu(MenuDTO menuDTO) {
        Menu menu = convertToEntity(menuDTO);
        return convertToDTO(menuRepository.save(menu));
    }

    @Override
    public MenuDTO updateMenu(Long id, MenuDTO menuDTO) {
        Menu existingMenu = menuRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Menu not found with id: " + id));
        
        Menu updatedMenu = convertToEntity(menuDTO);
        existingMenu.setName(updatedMenu.getName());
        existingMenu.setUrl(updatedMenu.getUrl());
        existingMenu.setType(updatedMenu.getType());
        existingMenu.setParentId(updatedMenu.getParentId());
        existingMenu.setSortOrder(updatedMenu.getSortOrder());
        existingMenu.setActive(updatedMenu.isActive());
        
        return convertToDTO(menuRepository.save(existingMenu));
    }

    @Override
    public void deleteMenu(Long id) {
        Menu menu = menuRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Menu not found with id: " + id));
        menuRepository.delete(menu);
    }

    @Override
    public void changeMenuOrder(Long id, int newOrder) {
        Menu menu = menuRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Menu not found with id: " + id));
        
        int oldOrder = menu.getSortOrder();
        if (newOrder > oldOrder) {
            menuRepository.decreaseOrderBetween(oldOrder + 1, newOrder);
        } else if (newOrder < oldOrder) {
            menuRepository.increaseOrderBetween(newOrder, oldOrder - 1);
        }
        
        menu.setSortOrder(newOrder);
        menuRepository.save(menu);
    }

    @Override
    public void toggleMenuStatus(Long id) {
        Menu menu = menuRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Menu not found with id: " + id));
        menu.setActive(!menu.isActive());
        menuRepository.save(menu);
    }

    @Override
    public List<MenuDTO> getActiveMenus() {
        return menuRepository.findByIsActiveTrueOrderBySortOrderAsc()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private MenuDTO convertToDTO(Menu menu) {
        return new MenuDTO(
                menu.getId(),
                menu.getName(),
                menu.getUrl(),
                menu.getType(),
                menu.getParentId(),
                menu.getSortOrder(),
                menu.isActive()
        );
    }

    private Menu convertToEntity(MenuDTO menuDTO) {
        Menu menu = new Menu();
        menu.setName(menuDTO.name());
        menu.setUrl(menuDTO.url());
        menu.setType(menuDTO.type());
        menu.setParentId(menuDTO.parentId());
        menu.setSortOrder(menuDTO.sortOrder());
        menu.setActive(menuDTO.isActive());
        return menu;
    }
} 