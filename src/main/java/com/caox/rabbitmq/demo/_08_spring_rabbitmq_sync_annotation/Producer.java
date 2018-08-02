package com.caox.rabbitmq.demo._08_spring_rabbitmq_sync_annotation;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Queue;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
/**
 * Created by nazi on 2018/7/27.
 */

public class Producer {
    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(AnnotationConfiguration.class);

        // 声明创建队列
        AmqpAdmin amqpAdmin = context.getBean(AmqpAdmin.class);
        Queue helloWorldQueue = new Queue("create.world.queue");
        amqpAdmin.declareQueue(helloWorldQueue);

        AmqpTemplate amqpTemplate = context.getBean(AmqpTemplate.class);
        amqpTemplate.convertAndSend("Hello World");
        System.out.println("Sent: Hello World");
    }
}

