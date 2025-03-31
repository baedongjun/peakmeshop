package com.peakmeshop.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        // 허용할 오리진 설정 (프론트엔드 URL)
        config.addAllowedOrigin("http://localhost");

        // 허용할 HTTP 메서드 설정
        config.addAllowedMethod("*");

        // 허용할 헤더 설정
        config.addAllowedHeader("*");

        // 인증 정보 포함 허용
        config.setAllowCredentials(true);

        // 모든 경로에 대해 CORS 설정 적용
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}