package com.peakmeshop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
@EnableScheduling
@EnableJpaRepositories
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

