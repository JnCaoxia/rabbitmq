package com.caox.rabbitmq.demo._08_spring_rabbitmq_sync_annotation;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Created by nazi on 2018/7/27.
 */

public class Consumer {
    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(AnnotationConfiguration.class);
        AmqpTemplate amqpTemplate = context.getBean(AmqpTemplate.class);
        System.out.println("Received: " + amqpTemplate.receiveAndConvert());
    }
}

