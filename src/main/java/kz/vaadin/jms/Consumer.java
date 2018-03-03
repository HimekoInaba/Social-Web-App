package kz.vaadin.jms;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.ObjectMessage;

import kz.vaadin.model.User;
import org.springframework.jms.core.JmsTemplate;

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

    public String receiveMessage() throws JMSException {
        ObjectMessage objectMessage = (ObjectMessage) jmsTemplate.receive(destination);
        User user = (User) objectMessage.getObject();
        return user.getUsername();
    }
}
