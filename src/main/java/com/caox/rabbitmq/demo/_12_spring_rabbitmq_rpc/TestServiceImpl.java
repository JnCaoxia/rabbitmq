package com.caox.rabbitmq.demo._12_spring_rabbitmq_rpc;

/**
 * Created by nazi on 2018/7/30.
 */

public class TestServiceImpl implements TestService {

    public String say(String msg) {
        return "hello "+msg;
    }
}
