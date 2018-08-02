package com.caox.rabbitmq.demo._16_rabbitmq_distributed_delay_retry_queue_ttl;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Consumer;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * Created by nazi on 2018/8/2.
 * 基于 RabbitMQ 实现的分布式延迟重试队列
 */
@Slf4j
public class RabbitMQDelayQueue {
    private static Logger LOGGER = LoggerFactory.getLogger(RabbitMQDelayQueue.class);

    private static final String POSTFIX_TASK = "_task";
    // direct类型 交换器
    public static final String EXCHANGE_TYPE_DIRECT = "direct";

    private Connection connection;
    //注册消费者
    private ConsumerRegister consumerRegister;

    //任务队列配置
    private String taskExchangeName;
    private String taskQueueName;
    private String taskRoutingKeyName;

    //延迟队列配置
    private String delayExchangeName;
    private String delayQueueName;
    private String delayRoutingKeyName;

    //延迟队列中的消息ttl
    private long perDelayQueueMessageTTL;

    public RabbitMQDelayQueue(Connection connection, ConsumerRegister consumerRegister, String delayExchangeName,
                              String delayQueueName, String delayRoutingKeyName, long perDelayQueueMessageTTL) throws Exception {
        this.connection = connection;
        this.consumerRegister = consumerRegister;
        this.delayExchangeName = delayExchangeName;
        this.delayQueueName = delayQueueName;
        this.delayRoutingKeyName = delayRoutingKeyName;
        this.perDelayQueueMessageTTL = perDelayQueueMessageTTL;
        this.taskExchangeName = delayExchangeName + POSTFIX_TASK;
        this.taskQueueName = delayQueueName + POSTFIX_TASK;
        this.taskRoutingKeyName = delayRoutingKeyName + POSTFIX_TASK;
        init();
        registerConsumer();
    }

    /**
     * @Description 注册消费者
     */
    public interface ConsumerRegister {
        public Consumer register(Channel channel) throws IOException;
    }

    /**
     * 注册带有ttl的queue和对应的任务队列
     * @throws IOException
     */
    private void init() throws Exception {
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(taskExchangeName, EXCHANGE_TYPE_DIRECT, true);
        channel.exchangeDeclare(delayExchangeName, EXCHANGE_TYPE_DIRECT, true);

        // 任务队列 B
        HashMap<String, Object> argumentsTask = Maps.newHashMap();
        argumentsTask.put("x-dead-letter-exchange", delayExchangeName);
        argumentsTask.put("x-dead-letter-routing-key", delayRoutingKeyName);
        channel.queueDeclare(taskQueueName, true, false, false, argumentsTask);
        channel.queueBind(taskQueueName, taskExchangeName, taskRoutingKeyName);

        // 延迟队列 A
        HashMap<String, Object> argumentsDelay = Maps.newHashMap();
        argumentsDelay.put("x-dead-letter-exchange", taskExchangeName);
        argumentsDelay.put("x-dead-letter-routing-key", taskRoutingKeyName);
        argumentsDelay.put("x-message-ttl", perDelayQueueMessageTTL);
        channel.queueDeclare(delayQueueName, true, false, false, argumentsDelay);
        channel.queueBind(delayQueueName, delayExchangeName, delayRoutingKeyName);

        channel.close();
    }

    /**
     * 注册消费者
     * @throws IOException
     * @author roc
     */
    private void registerConsumer() throws IOException {
        LOGGER.info("register consumer ->{}", this);
        Channel channel = connection.createChannel();
        Consumer consumer = consumerRegister.register(channel);
        channel.basicConsume(taskQueueName, false, consumer);
        LOGGER.info("register consumer ->{} success", this);
    }

    /**
     * 消息入队
     *
     * @param body 消息内容
     * @param timeout 超时时间
     * @param unit 超时时间单位
     * @throws IOException
     * @author roc
     */
    public void put(byte[] body, long timeout, TimeUnit unit) throws Exception {

        Preconditions.checkNotNull(body);
        Preconditions.checkArgument(timeout >= 0);
        Preconditions.checkNotNull(unit);

        LOGGER.info("put element to delay queue ->{}", body.hashCode());
        Channel channel = null;
        try {
            channel = connection.createChannel();
            // deliveryMode=2 标识任务的持久性
            long millis = unit.toMillis(timeout);
            AMQP.BasicProperties properties = new AMQP.BasicProperties.Builder().expiration(String.valueOf(millis)).deliveryMode(2).build();
            channel.basicPublish(delayExchangeName, delayRoutingKeyName, properties, body);
            LOGGER.info("put element to delay queue success");
        } finally {
            if (null != channel){
                channel.close();
            }
        }
    }

    public static class Builder {

        private Connection connection;
        private ConsumerRegister consumerRegister;

        private String delayExchangeName;
        private String delayQueueName;
        private String delayRoutingKeyName;

        private long perDelayQueueMessageTTL;

        public Builder setConnection(Connection connection) {
            this.connection = connection;
            return this;
        }

        public Builder setDelayExchangeName(String delayExchangeName) {
            this.delayExchangeName = delayExchangeName;
            return this;
        }

        public Builder setDelayQueueName(String delayQueueName) {
            this.delayQueueName = delayQueueName;
            return this;
        }

        public Builder setDelayRoutingKeyName(String delayRoutingKeyName) {
            this.delayRoutingKeyName = delayRoutingKeyName;
            return this;
        }

        public Builder setConsumerRegister(ConsumerRegister consumerRegister) {
            this.consumerRegister = consumerRegister;
            return this;
        }

        public Builder setPerDelayQueueMessageTTL(long timeout, TimeUnit unit) {
            this.perDelayQueueMessageTTL = unit.toMillis(timeout);;
            return this;
        }

        public RabbitMQDelayQueue build() throws Exception {
            Preconditions.checkNotNull(connection);
            Preconditions.checkNotNull(delayExchangeName);
            Preconditions.checkNotNull(delayQueueName);
            Preconditions.checkNotNull(delayRoutingKeyName);
            Preconditions.checkNotNull(consumerRegister);
            return new RabbitMQDelayQueue(connection, consumerRegister, delayExchangeName,
                    delayQueueName, delayRoutingKeyName, perDelayQueueMessageTTL);
        }

    }

}
