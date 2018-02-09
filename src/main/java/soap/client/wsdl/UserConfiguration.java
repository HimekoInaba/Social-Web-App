package soap.client.wsdl;

import org.springframework.context.annotation.Bean;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

public class UserConfiguration {

    @Bean
    public Jaxb2Marshaller marshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setContextPath("soap.client.wsdl");
        return marshaller;
    }

    @Bean
    public UserClient userClient(Jaxb2Marshaller marshaller) {
        UserClient client  = new UserClient();
        client.setDefaultUri("http://localhost:8080/ws");
        client.setMarshaller(marshaller);
        client.setUnmarshaller(marshaller);
        return client;
    }

}
