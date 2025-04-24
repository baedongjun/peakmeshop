package com.peakmeshop.domain.service.impl;

import com.peakmeshop.api.dto.AdminMenuDTO;
import com.peakmeshop.domain.entity.AdminMenu;
import com.peakmeshop.domain.repository.AdminMenuRepository;
import com.peakmeshop.domain.service.AdminMenuService;
import com.peakmeshop.api.mapper.AdminMenuMapper;
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
    private final AdminMenuMapper adminMenuMapper;

    @Override
    public List<AdminMenuDTO> getAllMenus() {
        List<AdminMenu> menus = adminMenuRepository.findAll();
        return menus.stream()
                .map(adminMenuMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<AdminMenuDTO> getRootMenus() {
        List<AdminMenu> menus = adminMenuRepository.findByParentIsNull();
        return menus.stream()
                .map(adminMenuMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<AdminMenuDTO> getChildMenus(Long parentId) {
        List<AdminMenu> menus = adminMenuRepository.findByParentId(parentId);
        return menus.stream()
                .map(adminMenuMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AdminMenuDTO createMenu(AdminMenuDTO menuDTO) {
        AdminMenu menu = adminMenuMapper.toEntity(menuDTO);
        menu.setCreatedAt(LocalDateTime.now());
        menu.setUpdatedAt(LocalDateTime.now());

        if (menuDTO.getParentId() != null) {
            AdminMenu parent = adminMenuRepository.findById(menuDTO.getParentId())
                    .orElseThrow(() -> new EntityNotFoundException("Parent menu not found with id: " + menuDTO.getParentId()));
            menu.setParent(parent);
        }

        menu = adminMenuRepository.save(menu);
        return adminMenuMapper.toDTO(menu);
    }

    @Override
    @Transactional
    public AdminMenuDTO updateMenu(Long id, AdminMenuDTO menuDTO) {
        AdminMenu menu = adminMenuRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Menu not found with id: " + id));

        AdminMenu updatedMenu = adminMenuMapper.toEntity(menuDTO);
        updatedMenu.setId(menu.getId());
        updatedMenu.setCreatedAt(menu.getCreatedAt());
        updatedMenu.setUpdatedAt(LocalDateTime.now());

        if (menuDTO.getParentId() != null) {
            AdminMenu parent = adminMenuRepository.findById(menuDTO.getParentId())
                    .orElseThrow(() -> new EntityNotFoundException("Parent menu not found with id: " + menuDTO.getParentId()));
            updatedMenu.setParent(parent);
        }

        menu = adminMenuRepository.save(updatedMenu);
        return adminMenuMapper.toDTO(menu);
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
                    menu.setUpdatedAt(LocalDateTime.now());
                    
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
                .map(adminMenuMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<AdminMenuDTO> getActiveMenus() {
        List<AdminMenu> menus = adminMenuRepository.findByIsVisibleTrue();
        return menus.stream()
                .map(adminMenuMapper::toDTO)
                .collect(Collectors.toList());
    }
} 