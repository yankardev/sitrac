package com.yankardev.sitrac.notificacion.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    public static final String EXCHANGE = "sitrac.events";
    public static final String QUEUE = "sitrac.notificaciones";

    @Bean
    TopicExchange sitracExchange() {
        return new TopicExchange(EXCHANGE, true, false);
    }

    @Bean
    Queue notificacionesQueue() {
        return new Queue(QUEUE, true);
    }

    @Bean
    Binding programacionBinding(Queue notificacionesQueue, TopicExchange sitracExchange) {
        return BindingBuilder.bind(notificacionesQueue).to(sitracExchange).with("programacion.*");
    }

    @Bean
    Binding viajeBinding(Queue notificacionesQueue, TopicExchange sitracExchange) {
        return BindingBuilder.bind(notificacionesQueue).to(sitracExchange).with("viaje.*");
    }

    @Bean
    MessageConverter rabbitJsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }
}
