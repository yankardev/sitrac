package com.yankardev.sitrac.comercial;

import com.yankardev.sitrac.cliente.ClienteServiceApplication;
import com.yankardev.sitrac.pedido.PedidoServiceApplication;
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
                "com.yankardev.sitrac.comercial",
                "com.yankardev.sitrac.cliente",
                "com.yankardev.sitrac.pedido"
        },
        nameGenerator = FullyQualifiedAnnotationBeanNameGenerator.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {ClienteServiceApplication.class, PedidoServiceApplication.class}
        )
)
@EntityScan(basePackages = {
        "com.yankardev.sitrac.cliente.infrastructure.adapter.out.persistence",
        "com.yankardev.sitrac.pedido.infrastructure.adapter.out.persistence"
})
@EnableJpaRepositories(basePackages = {
        "com.yankardev.sitrac.cliente.infrastructure.adapter.out.persistence",
        "com.yankardev.sitrac.pedido.infrastructure.adapter.out.persistence"
})
public class ComercialServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ComercialServiceApplication.class, args);
    }
}
