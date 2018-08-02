package com.caox.rabbitmq.demo._16_rabbitmq_distributed_delay_retry_queue_ttl;

import com.rabbitmq.client.*;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by nazi on 2018/8/2.
 */
public class RabbitMQDelayQueueTest {
    public static void main(String[] args) throws Exception {
        delayQueue();
    }

    public static void delayQueue() throws Exception {

        ConnectionFactory factory = new ConnectionFactory();
        factory.setUsername("caoxia");
        factory.setPassword("caoxia123456");
        Address address = new Address("127.0.0.1", 5672);
        Connection connection = factory.newConnection(new Address[] { address });

        RabbitMQDelayQueue delayQueue = new RabbitMQDelayQueue.Builder()
                .setConnection(connection)
                .setPerDelayQueueMessageTTL(15, TimeUnit.SECONDS)
                .setDelayExchangeName("delay_exchange_roc")
                .setDelayQueueName("delay_queue_roc")
                .setDelayRoutingKeyName("delay_routing_key_roc")
                .setConsumerRegister
                        (new RabbitMQDelayQueue.ConsumerRegister() {
//                            @Override
                            public Consumer register(Channel channel) throws IOException {
                                return new DefaultConsumer(channel) {
//                                    @Override
                                    public void handleDelivery(String consumerTag, Envelope envelope,
                                                               BasicProperties properties, byte[] body) throws IOException {
                                        long deliveryTag = envelope.getDeliveryTag();
                                        System.out.println(deliveryTag);
                                        String exchange = envelope.getExchange();
                                        String routingKey = envelope.getRoutingKey();
                                        // TODO do something
                                        String content = new String(body, Charset.forName("utf-8"));
                                        System.out.println("receive message --- > " + content);

                                        Map<String, Object> headers = properties.getHeaders();
                                        if (headers != null) {
                                            List<Map<String, Object>> xDeath = (List<Map<String, Object>>) headers.get("x-death");
                                            System.out.println("xDeath--- > " + xDeath);
                                            if (xDeath != null && !xDeath.isEmpty()) {
                                                Map<String, Object> entrys = xDeath.get(0);
                                            }
                                        }
                                        // 消息拒收
                                        // if(do something) 消息重新入队
//                                        getChannel().basicReject(deliveryTag, false);
                                        // else 消息应答
                                         getChannel().basicAck(deliveryTag, false);
                                    }
                                };
                            }
                        }).build();

        delayQueue.put("{\"name\" : \"i am roc!!\"}\"".getBytes("UTF-8"), 3, TimeUnit.SECONDS);

    }

}
