package com.caox.rabbitmq.demo._09_spring_rabbitmq_async_xml;

/**
 * Created by nazi on 2018/7/27.
 */
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Receive {

    public static void main(String[] args) {
        new ClassPathXmlApplicationContext("applicationContext-rabbitmq-async-receive.xml");
    }
}
