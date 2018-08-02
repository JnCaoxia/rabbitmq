package com.caox.rabbitmq.demo._02_workqueue;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

/**
 * Created by nazi on 2018/7/24.
 */

public class NewTask {
    //队列名称
    private final static String QUEUE_NAME = "workqueue_durable";

    /**
     *  (一)：发消息的方式叫做round-robin机制  NewTask  + Work1 + Work2 + Work3 特点：一次性分配
     * （二）：消息应答机制
     * （三）:消息持久化机制
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        //创建连接和频道
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        /**
         * 消息持久化
         * 第一， 我们需要确认RabbitMQ永远不会丢失我们的队列。为了这样，我们需要声明它为持久化的。
         * boolean durable = true;
         * channel.queueDeclare("task_queue", durable, false, false, null);
         * 注：RabbitMQ不允许使用不同的参数重新定义一个队列，所以已经存在的队列，我们无法修改其属性
         *
         */
        boolean durable = true;
        //声明队列
        channel.queueDeclare(QUEUE_NAME, durable, false, false, null);
        //发送10条消息，依次在消息后面附加1-10个点
        for (int i = 0; i < 5; i++)
        {
            String dots = "";
            for (int j = 0; j <= i; j++) {
                dots += ".";
            }
            String message = "helloworld" + dots+dots.length();
            /**
             * 第二， 我们需要标识我们的信息为持久化的。通过设置MessageProperties（implements BasicProperties）值为PERSISTENT_TEXT_PLAIN
             */
//            channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
            channel.basicPublish("", QUEUE_NAME, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes());
            System.out.println(" [x] Sent '" + message + "'");
        }
        //关闭频道和资源
        channel.close();
        connection.close();
    }


}



