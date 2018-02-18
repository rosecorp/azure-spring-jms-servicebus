package com.rosecorp;

import org.apache.qpid.jms.JmsConnectionFactory;
import org.apache.qpid.jms.JmsQueue;
import org.apache.qpid.jms.JmsTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.*;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.listener.SimpleMessageListenerContainer;

import javax.jms.*;

@Configuration
@ComponentScan
@EnableJms
@PropertySource("servicebus.properties")
public class TopicReceiverApp {

    private final Logger logger = LoggerFactory.getLogger(TopicReceiverApp.class);

    @Value("${servicebus.hostname}")
    private String hostName;

    @Value("${servicebus.username}")
    private String userName;

    @Value("${servicebus.password}")
    private String password;

    @Value("${servicebus.topic.subscriber}")
    private String topicSubscriber;

    @Bean
    ConnectionFactory getConnectionFactory() {
        ConnectionFactory connectionFactory = new JmsConnectionFactory(userName, password, hostName);
        return connectionFactory;
    }

    @Bean
    public SimpleMessageListenerContainer jmsListenerContainerFactory(ConnectionFactory connectionFactory, JmsTopic jmsTopic) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setConcurrency("1");
        container.setDestination(jmsTopic);
        container.setSessionAcknowledgeMode(Session.CLIENT_ACKNOWLEDGE);
        container.setSessionTransacted(false);
        container.setMessageListener(new MessageListener() {
            public void onMessage(Message message) {
                try {
                    TextMessage receivedMessage = (TextMessage) message;
                    System.out.println("============================"+ receivedMessage.getText());
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        });
        return container;
    }

    @Bean
    JmsTopic newJmsTopic() {
        return new JmsTopic(topicSubscriber);
    }

    public static void main(String[] args) throws InterruptedException {
        ApplicationContext context = new AnnotationConfigApplicationContext(TopicReceiverApp.class);
        Thread.sleep(1000000000);
    }


}
