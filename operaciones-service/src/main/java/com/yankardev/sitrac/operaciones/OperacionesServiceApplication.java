package com.yankardev.sitrac.operaciones;

import com.yankardev.sitrac.combustible.CombustibleServiceApplication;
import com.yankardev.sitrac.programacion.ProgramacionServiceApplication;
import com.yankardev.sitrac.viaje.ViajeServiceApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.FullyQualifiedAnnotationBeanNameGenerator;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(
        basePackages = {
                "com.yankardev.sitrac.operaciones",
                "com.yankardev.sitrac.programacion",
                "com.yankardev.sitrac.viaje",
                "com.yankardev.sitrac.combustible"
        },
        nameGenerator = FullyQualifiedAnnotationBeanNameGenerator.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {
                        ProgramacionServiceApplication.class,
                        ViajeServiceApplication.class,
                        CombustibleServiceApplication.class,
                        com.yankardev.sitrac.programacion.infrastructure.messaging.RabbitEventConfig.class,
                        com.yankardev.sitrac.viaje.infrastructure.messaging.RabbitEventConfig.class
                }
        )
)
@EntityScan(basePackages = {
        "com.yankardev.sitrac.programacion.infrastructure.adapter.out.persistence",
        "com.yankardev.sitrac.viaje.infrastructure.adapter.out.persistence",
        "com.yankardev.sitrac.combustible.infrastructure.adapter.out.persistence"
})
@EnableJpaRepositories(basePackages = {
        "com.yankardev.sitrac.programacion.infrastructure.adapter.out.persistence",
        "com.yankardev.sitrac.viaje.infrastructure.adapter.out.persistence",
        "com.yankardev.sitrac.combustible.infrastructure.adapter.out.persistence"
})
@EnableFeignClients(basePackages = "com.yankardev.sitrac.programacion.infrastructure.adapter.out.rest.client")
public class OperacionesServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(OperacionesServiceApplication.class, args);
    }
}
