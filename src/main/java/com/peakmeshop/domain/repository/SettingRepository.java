package com.peakmeshop.domain.repository;

import com.peakmeshop.domain.entity.Setting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SettingRepository extends JpaRepository<Setting, String> {
} 