package com.peakmeshop.common.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.peakmeshop.domain.entity.Role;
import com.peakmeshop.domain.entity.RoleName;
import com.peakmeshop.domain.repository.RoleRepository;

@Configuration
public class RoleConfig {

    @Bean
    public CommandLineRunner initRoles(RoleRepository roleRepository) {
        return args -> {
            // 기본 역할이 없으면 생성
            if (roleRepository.count() == 0) {
                roleRepository.save(Role.builder().name(RoleName.ROLE_USER).description("일반 사용자").build());
                roleRepository.save(Role.builder().name(RoleName.ROLE_ADMIN).description("관리자").build());
                roleRepository.save(Role.builder().name(RoleName.ROLE_MANAGER).description("매니저").build());
            }
        };
    }
}