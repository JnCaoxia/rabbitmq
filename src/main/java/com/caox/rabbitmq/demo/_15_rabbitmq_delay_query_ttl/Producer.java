package com.caox.rabbitmq.demo._15_rabbitmq_delay_query_ttl;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;


/**
 * Created by nazi on 2018/8/1.
 * 延时队列 队列配置延时
 */


public class Producer {

    private static String queue_name = "test.queue";

    public static void main(String[] args) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("127.0.0.1");
        factory.setUsername("caoxia");
        factory.setPassword("caoxia123456");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        // 声明队列
        channel.queueDeclare(queue_name, true, false, false, null);
        String message = "hello world!" + System.currentTimeMillis();
        channel.basicPublish("delaysync.exchange", "deal.message", null, message.getBytes());
        System.out.println("sent message: " + message + ",date:" + System.currentTimeMillis());
        // 关闭频道和连接
        channel.close();
        connection.close();
    }
}


