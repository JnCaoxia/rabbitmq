package com.caox.rabbitmq.demo._12_spring_rabbitmq_rpc;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by nazi on 2018/7/30.
 */
public class Client {

    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-rabbitmq-rpc/applicationContext-rabbitmq-rpc-client.xml");
        TestService testService = (TestService) context.getBean("testService");
        System.out.println(testService.say(" Tom"));
    }
}

