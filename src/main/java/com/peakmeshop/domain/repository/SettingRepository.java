package com.peakmeshop.domain.repository;

import com.peakmeshop.domain.entity.Setting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SettingRepository extends JpaRepository<Setting, Long> {
    Optional<Setting> findByKey(String key);
    List<Setting> findByGroup(String group);
    List<Setting> findByIsPublic(Boolean isPublic);
    boolean existsByKey(String key);
    List<Setting> findByGroupOrderByIdAsc(String group);
} 