package com.caox.rabbitmq.demo._11_spring_rabbitmq_async;

/**
 * Created by nazi on 2018/7/30.
 */

public class ReceiveMsgHandler {

    public void handleMessage(String text) {
        System.out.println("Received: " + text);
    }
}
