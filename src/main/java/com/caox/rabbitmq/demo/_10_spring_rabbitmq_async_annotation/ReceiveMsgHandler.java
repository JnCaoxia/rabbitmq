package com.caox.rabbitmq.demo._10_spring_rabbitmq_async_annotation;

/**
 * Created by nazi on 2018/7/27.
 */

public class ReceiveMsgHandler {

    public void handleMessage(String text) {
        System.out.println("Received: " + text);
    }
}

