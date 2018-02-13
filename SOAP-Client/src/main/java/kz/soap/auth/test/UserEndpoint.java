package kz.soap.auth.test;

import localhost._8080.test.GetUserRequest;
import localhost._8080.test.GetUserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;


@Endpoint
public class UserEndpoint {

    private static final String NAMESPACE_URI = "http://localhost:8080/test";

    @Autowired
    private UserService userService;

    private String exception;


    @Autowired
    public UserEndpoint(UserService userService) {
        this.userService = userService;
    }


    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getUserRequest")
    @ResponsePayload
    public GetUserResponse getResponse(@RequestPayload GetUserRequest request) {

        String username = request.getUsername();
        String password = request.getPassword();
        String confirmPassword = request.getConfirmPassword();
        String email = request.getEmail();

        exception = userService.add(new User(username, password, confirmPassword, email));

        GetUserResponse response = new GetUserResponse();

        response.setStatus("OK");

        if (exception != null) {
            response.setStatus("Fail " + exception);
        }

        return  response;
    }
}
