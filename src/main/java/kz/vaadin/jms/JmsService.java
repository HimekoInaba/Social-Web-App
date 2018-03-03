package kz.vaadin.jms;

import java.net.URI;

import kz.vaadin.client.rest.RestUser;
import kz.vaadin.model.User;
import org.apache.activemq.broker.BrokerFactory;
import org.apache.activemq.broker.BrokerService;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

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
            System.out.println("Consumer receives " + consumer.receiveMessage());

            //TODO Implement REST client

            /*Client client = ClientBuilder.newBuilder().newClient();
            WebTarget target = client.target("http://localhost:8081/rs");
            target = target.path("service").queryParam("User", "user");

            Invocation.Builder builder = target.request();
            Response response = builder.get();
            RestUser restUser = builder.get(RestUser.class);*/

        } finally {
            broker.stop();
            context.close();
        }
    }
}
