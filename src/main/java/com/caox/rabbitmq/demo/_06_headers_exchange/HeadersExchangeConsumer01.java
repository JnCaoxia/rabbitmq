package com.caox.rabbitmq.demo._06_headers_exchange;

import com.rabbitmq.client.*;
import org.springframework.amqp.core.ExchangeTypes;

import java.util.Hashtable;
import java.util.Map;

/**
 * Created by nazi on 2018/7/25.
 * 这里没有转发器 Exchange 的概念
 */
public class HeadersExchangeConsumer01 {
    private final static String EXCHANGE_NAME = "header-exchange";
    private final static String QUEUE_NAME = "header-queue1";

    @SuppressWarnings("all")
    public static void main(String[] args) throws Exception {
        // 区分不同工作进程的输出
        int hashCode = HeadersExchangeConsumer01.class.hashCode();
        // 创建连接和频道
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("127.0.0.1");
        // 指定用户 密码
        factory.setUsername("caoxia");
        factory.setPassword("caoxia123456");
        // 指定端口
        factory.setPort(AMQP.PROTOCOL.PORT);
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        //声明转发器和类型headers
        channel.exchangeDeclare(EXCHANGE_NAME, ExchangeTypes.HEADERS,false,true,null);
        channel.queueDeclare(QUEUE_NAME,false, false, true,null);
        System.out.println(hashCode
                + " [*] Waiting for messages. To exit press CTRL+C");

        Map<String, Object> headers = new Hashtable<String, Object>();
        //all any
        headers.put("x-match", "any");
        headers.put("aaa", "01234");
        // 为转发器指定队列，设置binding 绑定header键值对
        channel.queueBind(QUEUE_NAME, EXCHANGE_NAME,"", headers);
        QueueingConsumer consumer = new QueueingConsumer(channel);
        // 指定接收者，第二个参数为自动应答，无需手动应答
        channel.basicConsume(QUEUE_NAME, true, consumer);
        while (true) {
            QueueingConsumer.Delivery delivery = consumer.nextDelivery();
            String message = new String(delivery.getBody());
            System.out.println(message);
        }
    }
}
