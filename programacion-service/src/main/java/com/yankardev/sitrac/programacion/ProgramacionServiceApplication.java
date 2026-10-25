package com.yankardev.sitrac.programacion;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class ProgramacionServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProgramacionServiceApplication.class, args);
    }
}
