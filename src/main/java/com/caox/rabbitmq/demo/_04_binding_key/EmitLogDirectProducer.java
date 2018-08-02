package com.caox.rabbitmq.demo._04_binding_key;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.util.Random;
import java.util.UUID;

/**
 * Created by nazi on 2018/7/25.
 * Direct转换器 路由选择
 */
public class EmitLogDirectProducer {
    private static final String EXCHANGE_NAME = "ex_logs_direct";
    private static final String EXCHANGE_NAME_ALL = "ex_logs_direct_all";
    private static final String[] SEVERITIES = { "info", "warning", "error" };

    public static void main(String[] argv) throws Exception {
        // 创建连接和频道
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        // 声明转发器的类型
        channel.exchangeDeclare(EXCHANGE_NAME, "direct");
        channel.exchangeDeclare(EXCHANGE_NAME_ALL,"fanout");
        //发送6条消息
        for (int i = 0; i < 6; i++)
        {
            String severity = getSeverity();
            String message = severity + "_log :" + UUID.randomUUID().toString();
            // 发布消息至转发器，指定routingkey
            channel.basicPublish(EXCHANGE_NAME, severity, null, message
                    .getBytes());

            channel.basicPublish(EXCHANGE_NAME_ALL, severity, null, message.getBytes());
            System.out.println(" [x] Sent '" + message + "'");
        }

        channel.close();
        connection.close();
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
