package kz.vaadin.client.soap;

import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.springframework.ws.soap.client.core.SoapActionCallback;
import soap.client.wsdl.GetUserRequest;
import soap.client.wsdl.GetUserResponse;

public class UserClient extends WebServiceGatewaySupport {

    public UserClient(){
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setContextPath("soap.client.wsdl");
        setDefaultUri("http://localhost:8080/ws/users.wsdl");
        setMarshaller(marshaller);
        setUnmarshaller(marshaller);
    }

    public GetUserResponse setUser(String username, String password, String confirmPassword, String email) {

        GetUserRequest request = new GetUserRequest();

        request.setUsername(username);
        request.setPassword(password);
        request.setConfirmPassword(confirmPassword);
        request.setEmail(email);


        GetUserResponse response = (GetUserResponse) getWebServiceTemplate()
                .marshalSendAndReceive("http://localhost:8080/ws/users.wsdl",
                        request, new SoapActionCallback("http://localhost:8080/ws/"));

        return response;
    }
}
