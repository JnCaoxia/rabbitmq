package com.caox.rabbitmq.demo._04_binding_key;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;

import java.util.Random;

/**
 * Created by nazi on 2018/7/25.
 */
public class ReceiveLogDirectConsumer2 {
    private static final String EXCHANGE_NAME_ALL = "ex_logs_direct_all";
    // 日志等级 相当于枚举
    private static final String[] SEVERITIES = { "info", "warning", "error" };

    public static void main(String[] argv) throws Exception {
        // 创建连接和频道
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        // 声明direct类型转发器
        channel.exchangeDeclare(EXCHANGE_NAME_ALL, "fanout");

        String queueName = channel.queueDeclare().getQueue();
        String severity = getSeverity();
        // 指定binding_key
        channel.queueBind(queueName, EXCHANGE_NAME_ALL, "");
        System.out.println(" [*] Waiting for ALL logs. To exit press CTRL+C");

        QueueingConsumer consumer = new QueueingConsumer(channel);
        channel.basicConsume(queueName, true, consumer);

        while (true) {
            QueueingConsumer.Delivery delivery = consumer.nextDelivery();
            String message = new String(delivery.getBody());

            System.out.println(" [x] Received '" + message + "'");
        }
    }

    /**
     * 随机产生一种日志类型
     *
     * @return
     */
    private static String getSeverity()
    {
        Random random = new Random();
        int ranVal = random.nextInt(3);
        return SEVERITIES[ranVal];
    }
}
