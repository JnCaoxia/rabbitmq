package com.caox.rabbitmq.demo._09_spring_rabbitmq_async_xml;

/**
 * Created by nazi on 2018/7/27.
 */
public class ReceiveMsgHandler {
    public void handleMessage(String text) {
        System.out.println("Received: " + text);
    }
}
