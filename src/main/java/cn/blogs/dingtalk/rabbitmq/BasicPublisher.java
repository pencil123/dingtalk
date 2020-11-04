package cn.blogs.dingtalk.rabbitmq;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class BasicPublisher {
    private static final Logger log = LoggerFactory.getLogger(BasicPublisher.class);
    //定义 JSON 序列化和反序列化实例
    @Autowired
    private ObjectMapper objectMapper;
    //定义 RabbitMQ 消息操作组件
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private Environment env;

    public void sendMsg(String message) {
        if(!Strings.isNullOrEmpty(message)){
            try{
                //定义传输格式为 JSON 字符串
                rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
                //指定消息模型中的交换机
                rabbitTemplate.setExchange("exchange-name");
                //指定消息模型中的路由
                rabbitTemplate.setRoutingKey("routing-key");
                //将字符串转化为待发送的消息，即一串二进制的数据流
                Message msg = MessageBuilder.withBody(message.getBytes("utf-8")).build();
                //转化并发送消息
                rabbitTemplate.convertAndSend(msg);
                log.info("基本消息模型-生产者-发送消息：{}",message);
            } catch (Exception e){
                log.error("基本消息模型-生产者-发送消息异常：{}",message,e.fillInStackTrace());
            }
        }
    }
}
