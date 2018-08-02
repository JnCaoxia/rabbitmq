package com.caox.rabbitmq.demo._11_spring_rabbitmq_async;

/**
 * Created by nazi on 2018/7/30.
 */
 import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Receive {

    public static void main(String[] args) {
        new ClassPathXmlApplicationContext("spring-rabbitmq/applicationContext-rabbitmq-async-receive.xml");
    }
}

