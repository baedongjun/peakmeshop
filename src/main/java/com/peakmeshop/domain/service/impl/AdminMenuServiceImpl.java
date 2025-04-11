package com.peakmeshop.domain.service.impl;

import com.peakmeshop.api.dto.AdminMenuDTO;
import com.peakmeshop.domain.entity.AdminMenu;
import com.peakmeshop.domain.repository.AdminMenuRepository;
import com.peakmeshop.domain.service.AdminMenuService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminMenuServiceImpl implements AdminMenuService {

    private final AdminMenuRepository adminMenuRepository;

    @Override
    public List<AdminMenuDTO> getAllMenus() {
        List<AdminMenu> menus = adminMenuRepository.findAll();
        return menus.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<AdminMenuDTO> getRootMenus() {
        List<AdminMenu> menus = adminMenuRepository.findByParentIsNull();
        return menus.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<AdminMenuDTO> getChildMenus(Long parentId) {
        List<AdminMenu> menus = adminMenuRepository.findByParentId(parentId);
        return menus.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AdminMenuDTO createMenu(AdminMenuDTO menuDTO) {
        AdminMenu menu = convertToEntity(menuDTO);
        menu = adminMenuRepository.save(menu);
        return convertToDTO(menu);
    }

    @Override
    @Transactional
    public AdminMenuDTO updateMenu(Long id, AdminMenuDTO menuDTO) {
        AdminMenu menu = adminMenuRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Menu not found with id: " + id));

        menu.setName(menuDTO.getName());
        menu.setUrl(menuDTO.getUrl());
        menu.setIcon(menuDTO.getIcon());
        menu.setSortOrder(menuDTO.getSortOrder());
        menu.setIsVisible(menuDTO.getIsVisible());

        if (menuDTO.getParentId() != null) {
            AdminMenu parent = adminMenuRepository.findById(menuDTO.getParentId())
                    .orElseThrow(() -> new EntityNotFoundException("Parent menu not found with id: " + menuDTO.getParentId()));
            menu.setParent(parent);
        } else {
            menu.setParent(null);
        }

        menu = adminMenuRepository.save(menu);
        return convertToDTO(menu);
    }

    @Override
    @Transactional
    public void deleteMenu(Long id) {
        adminMenuRepository.deleteById(id);
    }

    @Override
    @Transactional
    public List<AdminMenuDTO> updateMenuOrder(List<AdminMenuDTO> menuDTOs) {
        List<AdminMenu> menus = menuDTOs.stream()
                .map(dto -> {
                    AdminMenu menu = adminMenuRepository.findById(dto.getId())
                            .orElseThrow(() -> new EntityNotFoundException("Menu not found with id: " + dto.getId()));
                    menu.setSortOrder(dto.getSortOrder());
                    
                    if (dto.getParentId() != null) {
                        AdminMenu parent = adminMenuRepository.findById(dto.getParentId())
                                .orElseThrow(() -> new EntityNotFoundException("Parent menu not found with id: " + dto.getParentId()));
                        menu.setParent(parent);
                    } else {
                        menu.setParent(null);
                    }
                    
                    return menu;
                })
                .collect(Collectors.toList());

        menus = adminMenuRepository.saveAll(menus);
        return menus.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<AdminMenuDTO> getActiveMenus() {
        List<AdminMenu> menus = adminMenuRepository.findByIsVisibleTrue();
        return menus.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private AdminMenuDTO convertToDTO(AdminMenu menu) {
        return AdminMenuDTO.builder()
                .id(menu.getId())
                .name(menu.getName())
                .url(menu.getUrl())
                .icon(menu.getIcon())
                .sortOrder(menu.getSortOrder())
                .isVisible(menu.getIsVisible())
                .parentId(menu.getParent() != null ? menu.getParent().getId() : null)
                .parentName(menu.getParent() != null ? menu.getParent().getName() : null)
                .createdAt(menu.getCreatedAt())
                .updatedAt(menu.getUpdatedAt())
                .children(menu.getChildren().stream()
                        .map(this::convertToDTO)
                        .collect(Collectors.toList()))
                .build();
    }

    private AdminMenu convertToEntity(AdminMenuDTO dto) {
        AdminMenu menu = new AdminMenu();
        menu.setName(dto.getName());
        menu.setUrl(dto.getUrl());
        menu.setIcon(dto.getIcon());
        menu.setSortOrder(dto.getSortOrder());
        menu.setIsVisible(dto.getIsVisible());

        if (dto.getParentId() != null) {
            AdminMenu parent = adminMenuRepository.findById(dto.getParentId())
                    .orElseThrow(() -> new EntityNotFoundException("Parent menu not found with id: " + dto.getParentId()));
            menu.setParent(parent);
        }

        return menu;
    }
} 