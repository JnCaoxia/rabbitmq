package com.caox.rabbitmq.demo._12_spring_rabbitmq_rpc;

/**
 * Created by nazi on 2018/7/30.
 */
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Server {

    public static void main(String[] args) {
        new ClassPathXmlApplicationContext("spring-rabbitmq-rpc/applicationContext-rabbitmq-rpc-server.xml");
    }
}

