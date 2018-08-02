package com.caox.rabbitmq.demo._02_workqueue;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

/**
 * Created by nazi on 2018/7/24.
 * 公平转发 一个一个分配 ,区别于 round-robin机制
 * 【详述】：
 * 这种模式下支持动态增加消费者，因为消息并没有发送出去，动态增加了消费者马上投入工作。
 * 而默认的转发机制会造成，即使动态增加了消费者，此时的消息已经分配完毕，无法立即加入工作，即使有很多未完成的任务。
 */
public class FairDispatchProducer {
    // 队列名称
    private final static String QUEUE_NAME = "workqueue_persistence";

    public static void main(String[] args) throws Exception {
        // 创建连接和频道
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        // 声明队列
        // 1、设置队列持久化
        boolean durable = true;
        channel.queueDeclare(QUEUE_NAME, durable, false, false, null);
        // 发送10条消息，依次在消息后面附加1-10个点   6 -> 2
        for (int i = 5; i > 0; i--)
        {
            String dots = "";
            for (int j = 0; j <= i; j++)
            {
                dots += ".";
            }
            String message = "helloworld" + dots + dots.length();
            // MessageProperties 2、设置消息持久化
            channel.basicPublish("", QUEUE_NAME,
                    MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes());
            System.out.println(" [x] Sent '" + message + "'");
        }
        // 关闭频道和资源
        channel.close();
        connection.close();

    }

}
