package kz.vaadin.jms;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.ObjectMessage;

import kz.vaadin.model.User;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.client.RestTemplate;

public class Consumer {

    private JmsTemplate jmsTemplate;
    private Destination destination;

    public JmsTemplate getJmsTemplate() {
        return jmsTemplate;
    }

    public void setJmsTemplate(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    public Destination getDestination() {
        return destination;
    }

    public void setDestination(Destination destination) {
        this.destination = destination;
    }

    public User receiveMessage() throws JMSException {
        ObjectMessage objectMessage = (ObjectMessage) jmsTemplate.receive(destination);
        User user = (User) objectMessage.getObject();

        final String uri = "http://localhost:8082/newuser";

        RestTemplate restTemplate = new RestTemplate();
        User result = restTemplate.postForObject( uri, user, User.class);

        System.out.println(result);

        return user;
    }
}
