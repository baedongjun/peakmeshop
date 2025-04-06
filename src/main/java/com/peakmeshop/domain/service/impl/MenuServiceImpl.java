package com.peakmeshop.domain.service.impl;

import com.peakmeshop.api.dto.MenuDTO;
import com.peakmeshop.domain.entity.Menu;
import com.peakmeshop.domain.repository.MenuRepository;
import com.peakmeshop.domain.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MenuServiceImpl implements MenuService {

    private final MenuRepository menuRepository;

    @Override
    public List<MenuDTO> getMenusByType(String type) {
        return menuRepository.findByTypeOrderByOrderAsc(type)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public MenuDTO createMenu(MenuDTO menuDTO) {
        Menu menu = convertToEntity(menuDTO);
        menu = menuRepository.save(menu);
        return convertToDTO(menu);
    }

    @Override
    @Transactional
    public MenuDTO updateMenu(Long id, MenuDTO menuDTO) {
        Menu menu = menuRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Menu not found"));
        
        menu.setName(menuDTO.getName());
        menu.setType(menuDTO.getType());
        menu.setUrl(menuDTO.getUrl());
        menu.setIcon(menuDTO.getIcon());
        menu.setOrder(menuDTO.getOrder());
        menu.setIsActive(menuDTO.getIsActive());
        menu.setTarget(menuDTO.getTarget());
        
        if (menuDTO.getParentId() != null) {
            Menu parent = menuRepository.findById(menuDTO.getParentId())
                    .orElseThrow(() -> new RuntimeException("Parent menu not found"));
            menu.setParent(parent);
        } else {
            menu.setParent(null);
        }

        menu = menuRepository.save(menu);
        return convertToDTO(menu);
    }

    @Override
    @Transactional
    public void deleteMenu(Long id) {
        menuRepository.deleteById(id);
    }

    @Override
    @Transactional
    public List<MenuDTO> updateMenuOrder(List<MenuDTO> menuDTOs) {
        List<Menu> menus = menuDTOs.stream()
                .map(dto -> {
                    Menu menu = menuRepository.findById(dto.getId())
                            .orElseThrow(() -> new RuntimeException("Menu not found"));
                    menu.setOrder(dto.getOrder());
                    return menu;
                })
                .collect(Collectors.toList());

        menus = menuRepository.saveAll(menus);
        return menus.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private MenuDTO convertToDTO(Menu menu) {
        MenuDTO dto = new MenuDTO();
        dto.setId(menu.getId());
        dto.setName(menu.getName());
        dto.setType(menu.getType());
        dto.setUrl(menu.getUrl());
        dto.setIcon(menu.getIcon());
        dto.setOrder(menu.getOrder());
        dto.setIsActive(menu.getIsActive());
        dto.setTarget(menu.getTarget());
        if (menu.getParent() != null) {
            dto.setParentId(menu.getParent().getId());
        }
        return dto;
    }

    private Menu convertToEntity(MenuDTO dto) {
        Menu menu = new Menu();
        menu.setName(dto.getName());
        menu.setType(dto.getType());
        menu.setUrl(dto.getUrl());
        menu.setIcon(dto.getIcon());
        menu.setOrder(dto.getOrder());
        menu.setIsActive(dto.getIsActive());
        menu.setTarget(dto.getTarget());
        
        if (dto.getParentId() != null) {
            Menu parent = menuRepository.findById(dto.getParentId())
                    .orElseThrow(() -> new RuntimeException("Parent menu not found"));
            menu.setParent(parent);
        }
        
        return menu;
    }
} 