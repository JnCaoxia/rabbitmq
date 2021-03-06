package com.caox.rabbitmq.demo._08_spring_rabbitmq_sync;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by nazi on 2018/7/27.
 * 消费者
 */
public class Consumer {

    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
        AmqpTemplate amqpTemplate = context.getBean(RabbitTemplate.class);
        System.out.println("Received: " + amqpTemplate.receiveAndConvert());
    }
}

