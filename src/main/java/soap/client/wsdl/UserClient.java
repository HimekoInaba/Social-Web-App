package soap.client.wsdl;

import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.client.core.WebServiceMessageCallback;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.springframework.ws.soap.SoapMessage;


public class UserClient extends WebServiceGatewaySupport {

    public GetUserResponse getUser(String username, String password, String confirmPassword, String email) {

        GetUserRequest request = new GetUserRequest();
        request.setUsername(username);
        request.setPassword(password);
        request.setConfirmPassword(confirmPassword);
        request.setEmail(email);


        GetUserResponse response = (GetUserResponse) getWebServiceTemplate()
                .marshalSendAndReceive("http://localhost:8080/ws",
                        request, new WebServiceMessageCallback(){
                            public void doWithMessage(WebServiceMessage message) {
                                ((SoapMessage)message).setSoapAction("http://localhost:8080/ws/getUser");
                            }
                        });


        return response;
    }
}
