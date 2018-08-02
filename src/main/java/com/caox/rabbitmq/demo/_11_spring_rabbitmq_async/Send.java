//package com.caox.rabbitmq.demo._11_spring_rabbitmq_async;
//
///**
// * Created by nazi on 2018/7/30.
// */
//import org.springframework.context.ApplicationContext;
//import org.springframework.context.support.ClassPathXmlApplicationContext;
//
//import com.rabbitmq.spring.template.ASyncRabbitTemplate;
//
//public class Send {
//
//    public static void main(String[] args) throws InterruptedException {
//        ApplicationContext context = new ClassPathXmlApplicationContext("spring-rabbitmq/applicationContext-rabbitmq-async-send.xml");
//        ASyncRabbitTemplate amqpTemplate = context.getBean(ASyncRabbitTemplate.class);
//        for(int i=0;i<10000;i++){
//            amqpTemplate.send("test spring async=>"+i);
//            Thread.sleep(100);
//        }
//    }
//}
//
