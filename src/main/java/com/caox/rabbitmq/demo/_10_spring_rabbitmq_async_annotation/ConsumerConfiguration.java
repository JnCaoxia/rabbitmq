package com.caox.rabbitmq.demo._10_spring_rabbitmq_async_annotation;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.rabbitmq.client.AMQP;
/**
 * Created by nazi on 2018/7/27.
 */

@Configuration
public class ConsumerConfiguration {

    // 指定队列名称 routingkey的名称默认为Queue的名称，使用Exchange类型为DirectExchange
    protected String springQueueDemo = "spring-queue-async";

    // 创建链接
    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(
                "127.0.0.1");
        connectionFactory.setUsername("caoxia");
        connectionFactory.setPassword("caoxia123456");
        connectionFactory.setPort(AMQP.PROTOCOL.PORT);
        return connectionFactory;
    }

    // 创建rabbitAdmin 代理类
    @Bean
    public AmqpAdmin amqpAdmin() {
        return new RabbitAdmin(connectionFactory());
    }

    // 创建rabbitTemplate 消息模板类
    @Bean
    public RabbitTemplate rabbitTemplate() {
        RabbitTemplate template = new RabbitTemplate(connectionFactory());
        // The routing key is set to the name of the queue by the broker for the
        // default exchange.
        template.setRoutingKey(this.springQueueDemo);
        // Where we will synchronously receive messages from
        template.setQueue(this.springQueueDemo);
        return template;
    }

    //
    // Every queue is bound to the default direct exchange
    public Queue helloWorldQueue() {
        return new Queue(this.springQueueDemo);
    }

    @Bean
    public SimpleMessageListenerContainer listenerContainer() {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory());
        container.setQueueNames(this.springQueueDemo);
        container.setMessageListener(new MessageListenerAdapter(
                new ReceiveMsgHandler()));
        return container;
    }

}

