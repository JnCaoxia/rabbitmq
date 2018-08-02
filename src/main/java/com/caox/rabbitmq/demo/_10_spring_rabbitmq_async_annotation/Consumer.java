package com.caox.rabbitmq.demo._10_spring_rabbitmq_async_annotation;

/**
 * Created by nazi on 2018/7/27.
 */
 import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Consumer {
    public static void main(String[] args) {
        new AnnotationConfigApplicationContext(ConsumerConfiguration.class);
    }
}

