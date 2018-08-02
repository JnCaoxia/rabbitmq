package com.caox.rabbitmq.demo._13_spring_rabbitmq_label_xml;

import org.springframework.amqp.core.Message;

/**
 * Listener interface to receive asynchronous delivery of Amqp Messages.
 *
 * Created by nazi on 2018/7/30.
 */
public interface MessageListener {

    void onMessage(Message message);

}

