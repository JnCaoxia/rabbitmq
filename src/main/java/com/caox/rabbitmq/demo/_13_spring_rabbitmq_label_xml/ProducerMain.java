package com.caox.rabbitmq.demo._13_spring_rabbitmq_label_xml;

/**
 * Created by nazi on 2018/7/30.
 */

import com.caox.sharding.entity.User;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ProducerMain {

    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-rabbit-label/Producer.xml");
        AmqpTemplate amqpTemplate = context.getBean(RabbitTemplate.class);
        User user = new User();
        user.setName("niuniu");
        amqpTemplate.convertAndSend(user);
    }
}

