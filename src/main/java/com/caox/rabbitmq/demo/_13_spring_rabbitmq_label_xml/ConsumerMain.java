package com.caox.rabbitmq.demo._13_spring_rabbitmq_label_xml;

/**
 * Created by nazi on 2018/7/30.
 */
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ConsumerMain {

    public static void main(String[] args) {
        new ClassPathXmlApplicationContext("spring-rabbit-label/Consumer.xml");

    }
}

