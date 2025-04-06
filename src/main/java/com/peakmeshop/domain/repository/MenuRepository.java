package com.peakmeshop.domain.repository;

import com.peakmeshop.domain.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MenuRepository extends JpaRepository<Menu, Long> {
    List<Menu> findByTypeOrderByOrderAsc(String type);
    List<Menu> findByTypeAndIsActiveOrderByOrderAsc(String type, Boolean isActive);
} 