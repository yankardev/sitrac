package com.yankardev.sitrac.prevencion;

import com.yankardev.sitrac.somma.SommaServiceApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(
        basePackages = {
                "com.yankardev.sitrac.prevencion",
                "com.yankardev.sitrac.somma"
        },
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = SommaServiceApplication.class
        )
)
@EntityScan(basePackages = "com.yankardev.sitrac.somma.infrastructure.adapter.out.persistence")
@EnableJpaRepositories(basePackages = "com.yankardev.sitrac.somma.infrastructure.adapter.out.persistence")
public class PrevencionServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(PrevencionServiceApplication.class, args);
    }
}
