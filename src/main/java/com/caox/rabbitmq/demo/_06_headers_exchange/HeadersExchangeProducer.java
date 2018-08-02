package com.caox.rabbitmq.demo._06_headers_exchange;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.springframework.amqp.core.ExchangeTypes;

import java.util.Hashtable;
import java.util.Map;

/**
 * Created by nazi on 2018/7/25.
 * 相当于直接转发给队列 round-robin机制 + headers  发一份内容，这一份内容相同给到两份Consumer队列，消费者接受到的内容一样的
 * x-patch: all 满足 生产者和消费者的 参数要一模一样（个数和内容），多一个少一个都不行
 *
 */
public class HeadersExchangeProducer {
    private final static String EXCHANGE_NAME = "header-exchange";

    @SuppressWarnings("all")
    public static void main(String[] args) throws Exception {
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
        channel.exchangeDeclare(EXCHANGE_NAME, ExchangeTypes.HEADERS, false,true,null);
//        String message = new Date().toLocaleString() + " : log something";

        Map<String,Object> headers =  new Hashtable<String, Object>();
        headers.put("aaa", "01234");
        headers.put("abc","key");
        AMQP.BasicProperties.Builder properties = new AMQP.BasicProperties.Builder();
        properties.headers(headers);

        for (int i = 5; i > 0; i--) {
            String dots = "";
            for (int j = 0; j <= i; j++) {
                dots += ".";
            }
            String message = "helloworld" + dots + dots.length();
            // 指定消息发送到的转发器,绑定键值对headers键值对
            channel.basicPublish(EXCHANGE_NAME, "",properties.build(),message.getBytes());
            System.out.println(" [x] Sent '" + message + "'");
        }
        channel.close();
        connection.close();
    }
}
