package kz.vaadin.jms;

import java.net.URI;

import kz.vaadin.model.User;
import org.apache.activemq.broker.BrokerFactory;
import org.apache.activemq.broker.BrokerService;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class JmsService {

    public void start(User user) throws Exception {
        BrokerService broker = BrokerFactory.createBroker(new URI(
                "broker:(tcp://localhost:61616)"));
        broker.start();
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
                "jmsConf.xml");

        try {
            Producer producer = (Producer) context
                    .getBean("springJmsProducer");
            producer.sendMessage(user);

            Consumer consumer = (Consumer) context
                    .getBean("springJmsConsumer");
            System.out.println("Consumer receives " + consumer.receiveMessage().getUsername());

        } finally {
            broker.stop();
            context.close();
        }
    }
}
