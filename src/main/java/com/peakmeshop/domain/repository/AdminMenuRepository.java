package com.peakmeshop.domain.repository;

import com.peakmeshop.domain.entity.AdminMenu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdminMenuRepository extends JpaRepository<AdminMenu, Long> {
    /**
     * 상위 메뉴가 없는(최상위) 메뉴를 조회합니다.
     */
    List<AdminMenu> findByParentIsNull();

    /**
     * 특정 상위 메뉴의 하위 메뉴를 조회합니다.
     */
    List<AdminMenu> findByParentId(Long parentId);

    /**
     * 활성화된 메뉴만 조회합니다.
     */
    List<AdminMenu> findByIsVisibleTrue();

    /**
     * 상위 메뉴 ID로 하위 메뉴 수를 조회합니다.
     */
    int countByParentId(Long parentId);
} 