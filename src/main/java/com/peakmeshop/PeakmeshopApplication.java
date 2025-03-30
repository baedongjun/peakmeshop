package com.peakmeshop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.peakmeshop"}) // 이 패키지와 하위 패키지를 스캔
public class PeakmeshopApplication {
    public static void main(String[] args) {
        SpringApplication.run(PeakmeshopApplication.class, args);
    }
}
