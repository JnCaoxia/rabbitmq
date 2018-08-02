package com.caox.rabbitmq.demo._08_spring_rabbitmq_sync_annotation;

import com.rabbitmq.client.AMQP;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by nazi on 2018/7/27.
 */
@Configuration
public class AnnotationConfiguration {

    //指定队列名称 routingkey的名称默认为Queue的名称，使用Exchange类型为DirectExchange
    protected String springQueueDemo = "create.world.queue";

    //创建链接
    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory("127.0.0.1");
        connectionFactory.setUsername("caoxia");
        connectionFactory.setPassword("caoxia123456");
        connectionFactory.setPort(AMQP.PROTOCOL.PORT);
        return connectionFactory;
    }

    //创建rabbitAdmin 代理类
    @Bean
    public AmqpAdmin amqpAdmin() {
        return new RabbitAdmin(connectionFactory());
    }

    //创建rabbitTemplate 消息模板类
    @Bean
    public RabbitTemplate rabbitTemplate() {
        RabbitTemplate template = new RabbitTemplate(connectionFactory());
        //The routing key is set to the name of the queue by the broker for the default exchange.
        template.setRoutingKey(this.springQueueDemo);
        //Where we will synchronously receive messages from
        template.setQueue(this.springQueueDemo);
        return template;
    }

    //
    // Every queue is bound to the default direct exchange
    public Queue helloWorldQueue() {
        return new Queue(this.springQueueDemo);
    }

	/*
	@Bean
	public Binding binding() {
		return declare(new Binding(helloWorldQueue(), defaultDirectExchange()));
	}*/

	/*
	@Bean
	public TopicExchange helloExchange() {
		return declare(new TopicExchange("hello.world.exchange"));
	}*/

	/*
	public Queue declareUniqueQueue(String namePrefix) {
		Queue queue = new Queue(namePrefix + "-" + UUID.randomUUID());
		rabbitAdminTemplate().declareQueue(queue);
		return queue;
	}

	// if the default exchange isn't configured to your liking....
	@Bean Binding declareP2PBinding(Queue queue, DirectExchange exchange) {
		return declare(new Binding(queue, exchange, queue.getName()));
	}

	@Bean Binding declarePubSubBinding(String queuePrefix, FanoutExchange exchange) {
		return declare(new Binding(declareUniqueQueue(queuePrefix), exchange));
	}

	@Bean Binding declarePubSubBinding(UniqueQueue uniqueQueue, TopicExchange exchange) {
		return declare(new Binding(uniqueQueue, exchange));
	}

	@Bean Binding declarePubSubBinding(String queuePrefix, TopicExchange exchange, String routingKey) {
		return declare(new Binding(declareUniqueQueue(queuePrefix), exchange, routingKey));
	}*/
}
