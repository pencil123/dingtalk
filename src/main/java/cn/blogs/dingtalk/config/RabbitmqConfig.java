package cn.blogs.dingtalk.config;

import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.logging.Logger;

@Configuration
public class RabbitmqConfig {
    //定义日志
    private org.slf4j.Logger logger = LoggerFactory.getLogger(RabbitmqConfig.class);

    //自动装配 RabbitMQ 的链接工厂实例
    @Autowired
    private CachingConnectionFactory connectionFactory;
    //自动装配消息监听器所在的容器工厂配置类实例
    @Autowired
    private SimpleRabbitListenerContainerFactoryConfigurer factoryConfigurer;

    //下面为单一消费者实例的配置
    @Bean(name="singleListenerContainer")
    public SimpleRabbitListenerContainerFactory listenerContainer(){
        //定义消息监听器所在的容器工厂
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        //设置容器工厂所用的实例
        factory.setConnectionFactory(connectionFactory);
        //设置消息在传输中的格式，这里采用 JSON 的格式进行传输
        factory.setMessageConverter(new Jackson2JsonMessageConverter());
        //设置并发消费者实例的初始数量为1
        factory.setConcurrentConsumers(1);
        //设置并发消费者实例中最大数量为1
        factory.setMaxConcurrentConsumers(1);
        //设置并发消费者实例中每个实例拉取的消息数量为1个
        factory.setPrefetchCount(1);
        return factory;
    }

    //自定义配置 RabbitMQ 发送消息的操作组件 RabbitTemplate
    @Bean
    public RabbitTemplate rabbitTemplate(){
        //设置：发送消息后进行确认
        connectionFactory.setPublisherConfirms(true);
        //设置：发送消息后返回确认信息
        connectionFactory.setPublisherReturns(true);
        //构造发送消息组件实例对象
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);

        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
            @Override
            public void confirm(
                    CorrelationData correlationData, boolean b, String s) {
                if (b) {
                    logger.info("消息发送成功:correlationData({}),ack({}),cause({})", correlationData, b, s);
                } else {
                    System.out.println("消息确认失败");
                }
            }
        });
        // 设置消息收到确认
        rabbitTemplate.setMandatory(true);
/*    rabbitTemplate.setReturnCallback(new RabbitTemplate.ReturnCallback() {
    @Override
      public void returnedMessage(
          Message message, int i, String s, String s1, String s2) {
        log.info("消息发送失败");
      }
    });*/

        return rabbitTemplate;
    }


    // 定义读取配置文件的环境变量的实例
/*    @Autowired
    private Environment env;*/
    //创建队列
    @Bean(name = "basicQueue")
    public Queue basicQueue(){
        return new Queue("queue-name",true);
    }
    //创建交换机：以 DirectExchange 为例
    @Bean
    public DirectExchange basicExchange(){
        return new DirectExchange("exchange-name",true,false);
    }
    //绑定
    @Bean
    public Binding basicBinding(){
        return BindingBuilder.bind(basicQueue()).to(basicExchange()).
                with("routing-key");
    }
}
