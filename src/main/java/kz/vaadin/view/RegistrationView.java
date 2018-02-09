package kz.vaadin.view;

import com.vaadin.data.Binder;
import com.vaadin.event.ShortcutAction;
import com.vaadin.navigator.View;
import com.vaadin.ui.*;
import kz.vaadin.model.User;
import kz.vaadin.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.vaadin.spring.annotation.PrototypeScope;
import soap.client.wsdl.UserClient;


@PrototypeScope
@Component
public class RegistrationView extends VerticalLayout implements View {

    @Autowired
    UserService userService;

    @Autowired
    UserDetailsService userDetailsService;

    @Autowired
    LoginView loginView;

    @Autowired
    UserClient userClient;

    public RegistrationView() {

        Label label = new Label("Enter your information below to register:");
        TextField username = new TextField("Username");
        TextField email = new TextField("E-mail");
        PasswordField password = new PasswordField("Password");
        PasswordField confirmPassword = new PasswordField("Confirm password");
        Button register = new Button("Register");
        Button registerViaSOAP = new Button("Register via SOAP service");

        addComponents(label, username, password, confirmPassword, email, register, registerViaSOAP);

        setComponentAlignment(label, Alignment.MIDDLE_CENTER);
        setComponentAlignment(username, Alignment.MIDDLE_CENTER);
        setComponentAlignment(password, Alignment.MIDDLE_CENTER);
        setComponentAlignment(confirmPassword, Alignment.MIDDLE_CENTER);
        setComponentAlignment(email, Alignment.MIDDLE_CENTER);
        setComponentAlignment(register, Alignment.MIDDLE_CENTER);
        setComponentAlignment(registerViaSOAP, Alignment.MIDDLE_CENTER);

        new Binder<User>().forField(username)
                .withNullRepresentation("")
                .withValidator(s -> s.length() > 8, "Username length must be between at least 8 characters!")
                .withValidator(s -> s.length() < 32,"Username length must not exceed 32 characters!")
                .bind(User::getUsername, User::setUsername);

        new Binder<User>().forField(password)
                .withNullRepresentation("")
                .withValidator(s -> s.length() > 8, "Password length must be between at least 8 characters!")
                .withValidator(s -> s.length() < 32,"Password length must not exceed 32 characters!")
                .bind(User::getPassword, User::setPassword);

        new Binder<User>().forField(confirmPassword)
                .withNullRepresentation("")
                .withValidator(s -> s.length() > 8, "Confirmation password length must be between at least 8 characters!")
                .withValidator(s -> s.length() < 32,"Confirmation password length must not exceed 32 characters!")
                .bind(User::getConfirmPassword, User::setConfirmPassword);

        new Binder<User>().forField(email)
                .withNullRepresentation("")
                .bind(User::getEmail, User::setEmail);

        register.addClickListener(click -> {
            if(validate(username, password, confirmPassword, email)) {
                String exception = userService.add(new User(username.getValue(), password.getValue(),
                        confirmPassword.getValue(), email.getValue()));
                if (exception != null)
                    Notification.show(exception, Notification.Type.ERROR_MESSAGE);
                else {
                    userDetailsService.loadUserByUsername(username.getValue());
                    loginView.login(username.getValue(), password.getValue());
                }
            }
        });

        registerViaSOAP.addClickListener(click -> {
            if(validate(username, password, confirmPassword, email))
                userClient.getUser(username.getValue(), password.getValue(), confirmPassword.getValue(), email.getValue());
            else
                Notification.show("Error!", Notification.Type.ERROR_MESSAGE);
        });

        register.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        registerViaSOAP.setClickShortcut(ShortcutAction.KeyCode.ENTER);
    }

    private boolean validate(TextField username, PasswordField password, PasswordField confirmPassword, TextField email){
        byte check;

        if(username.getValue() !="" && password.getValue() != "" && confirmPassword.getValue() !="" && email.getValue() != "")
            check = 1;
        else
            check = 2;

        if (password.getValue().equals(confirmPassword.getValue())) {
            if (check == 1)
                return true;
        } else
            check = 4;

        if(check == 2) {
            Notification.show("Passwords don't match!", Notification.Type.ERROR_MESSAGE);
            return false;
        }

        if (check == 4) {
            Notification.show("All fields are mandatory!", Notification.Type.ERROR_MESSAGE);
            return false;
        }

        return false;
    }

}
