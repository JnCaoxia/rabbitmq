package com.caox.rabbitmq.demo._10_spring_rabbitmq_async_annotation;

import org.springframework.context.annotation.Configuration;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor;

import com.rabbitmq.client.AMQP;
/**
 * Created by nazi on 2018/7/27.
 */
@Configuration
public class ProducerConfiguration {

    // 指定队列名称 routingkey的名称默认为Queue的名称，使用Exchange类型为DirectExchange
    protected final String helloWorldQueueName = "spring-queue-async";

    // 创建链接
    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory("127.0.0.1");
        connectionFactory.setUsername("caoxia");
        connectionFactory.setPassword("caoxia123456");
        connectionFactory.setPort(AMQP.PROTOCOL.PORT);
        return connectionFactory;
    }

    // 创建rabbitTemplate 消息模板类
    @Bean
    public RabbitTemplate rabbitTemplate() {
        RabbitTemplate template = new RabbitTemplate(connectionFactory());
        template.setRoutingKey(this.helloWorldQueueName);
        return template;
    }

    //创建一个调度
    @Bean
    public ScheduledProducer scheduledProducer() {
        return new ScheduledProducer();
    }

    @Bean
    public BeanPostProcessor postProcessor() {
        return new ScheduledAnnotationBeanPostProcessor();
    }


    static class ScheduledProducer {

        @Autowired
        private volatile RabbitTemplate rabbitTemplate;

        //自增整数
        private final AtomicInteger counter = new AtomicInteger();
        /**
         * 每3秒发送一条消息
         *
         * Spring3中加强了注解的使用，其中计划任务也得到了增强，现在创建一个计划任务只需要两步就完成了：
         创建一个Java类，添加一个无参无返回值的方法，在方法上用@Scheduled注解修饰一下；
         在Spring配置文件中添加三个<task:**** />节点；
         参考：http://zywang.iteye.com/blog/949123
         */
        @Scheduled(fixedRate = 3000)
        public void sendMessage() {
            rabbitTemplate.convertAndSend("Hello World " + counter.incrementAndGet());
        }
    }

}

