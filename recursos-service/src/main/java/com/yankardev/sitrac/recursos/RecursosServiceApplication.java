package com.yankardev.sitrac.recursos;

import com.yankardev.sitrac.conductor.ConductorServiceApplication;
import com.yankardev.sitrac.flota.FlotaServiceApplication;
import com.yankardev.sitrac.mantenimiento.MantenimientoServiceApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.FullyQualifiedAnnotationBeanNameGenerator;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(
        basePackages = {
                "com.yankardev.sitrac.recursos",
                "com.yankardev.sitrac.conductor",
                "com.yankardev.sitrac.flota",
                "com.yankardev.sitrac.mantenimiento"
        },
        nameGenerator = FullyQualifiedAnnotationBeanNameGenerator.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {
                        ConductorServiceApplication.class,
                        FlotaServiceApplication.class,
                        MantenimientoServiceApplication.class
                }
        )
)
@EntityScan(basePackages = {
        "com.yankardev.sitrac.conductor.infrastructure.adapter.out.persistence",
        "com.yankardev.sitrac.flota.infrastructure.adapter.out.persistence",
        "com.yankardev.sitrac.mantenimiento.infrastructure.adapter.out.persistence"
})
@EnableJpaRepositories(basePackages = {
        "com.yankardev.sitrac.conductor.infrastructure.adapter.out.persistence",
        "com.yankardev.sitrac.flota.infrastructure.adapter.out.persistence",
        "com.yankardev.sitrac.mantenimiento.infrastructure.adapter.out.persistence"
})
public class RecursosServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(RecursosServiceApplication.class, args);
    }
}
