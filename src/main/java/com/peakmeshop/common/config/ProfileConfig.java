package com.peakmeshop.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;

@Configuration
public class ProfileConfig {

    private final Environment environment;

    @Value("${spring.application.name}")
    private String applicationName;

    public ProfileConfig(Environment environment) {
        this.environment = environment;
    }

    @Bean
    public String activeProfiles() {
        String[] activeProfiles = environment.getActiveProfiles();
        String currentProfile = activeProfiles.length > 0 ? activeProfiles[0] : "default";

        System.out.println("==============================================");
        System.out.println("애플리케이션 이름: " + applicationName);
        System.out.println("현재 활성화된 프로필: " + currentProfile);
        System.out.println("==============================================");

        return currentProfile;
    }

    @Bean
    @Profile("local")
    public String localSetup() {
        System.out.println("로컬 환경이 활성화되었습니다.");
        return "local";
    }

    @Bean
    @Profile("dev")
    public String devSetup() {
        System.out.println("개발 환경이 활성화되었습니다.");
        return "dev";
    }

    @Bean
    @Profile("prod")
    public String prodSetup() {
        System.out.println("운영 환경이 활성화되었습니다.");
        return "prod";
    }
}

