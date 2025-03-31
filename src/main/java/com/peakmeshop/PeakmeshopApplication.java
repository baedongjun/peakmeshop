package com.peakmeshop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
@ComponentScan(basePackages = {"com.peakmeshop"}) // 이 패키지와 하위 패키지를 스캔
public class PeakmeshopApplication {

    public static void main(String[] args) {
        SpringApplication.run(PeakmeshopApplication.class, args);
    }

    @Bean
    public WebMvcConfigurer webMvcConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addResourceHandlers(ResourceHandlerRegistry registry) {
                registry.addResourceHandler("/css/**").addResourceLocations("classpath:/static/css/");
                registry.addResourceHandler("/js/**").addResourceLocations("classpath:/static/js/");
                registry.addResourceHandler("/images/**").addResourceLocations("classpath:/static/images/");
                registry.addResourceHandler("/fonts/**").addResourceLocations("classpath:/static/fonts/");
            }
        };
    }
}

