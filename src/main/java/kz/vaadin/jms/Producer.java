package kz.vaadin.jms;

import javax.jms.Destination;

import kz.vaadin.model.User;
import org.springframework.jms.core.JmsTemplate;

public class Producer {

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

    public void sendMessage(final User user) {
        System.out.println("Producer sends " + user.getUsername());
        jmsTemplate.send(destination, session ->
                session.createObjectMessage(user));

    }

}
