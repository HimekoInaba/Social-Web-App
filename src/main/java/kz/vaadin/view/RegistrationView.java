package kz.vaadin.view;

import com.vaadin.data.Binder;
import com.vaadin.event.ShortcutAction;
import com.vaadin.navigator.View;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.*;
import kz.vaadin.model.User;
import kz.vaadin.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.vaadin.spring.annotation.PrototypeScope;


@PrototypeScope
@Component
public class RegistrationView extends VerticalLayout implements View {

    @Autowired
    UserService userService;

    @Autowired
    UserDetailsService userDetailsService;

    @Autowired
    LoginView loginView;

    public RegistrationView() {

        Label label = new Label("Enter your information below to register:");
        TextField username = new TextField("Username");
        PasswordField password = new PasswordField("Passoword");
        PasswordField confirmPassword = new PasswordField("Confirm password");
        TextField email = new TextField("E-mail");
        Button register = new Button("Register");

        addComponents(label, username, password, confirmPassword, email, register);

        setComponentAlignment(label, Alignment.MIDDLE_CENTER);
        setComponentAlignment(username, Alignment.MIDDLE_CENTER);
        setComponentAlignment(password, Alignment.MIDDLE_CENTER);
        setComponentAlignment(confirmPassword, Alignment.MIDDLE_CENTER);
        setComponentAlignment(email, Alignment.MIDDLE_CENTER);
        setComponentAlignment(register, Alignment.MIDDLE_CENTER);

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
            if(username.getValue() !="" && password.getValue() != "" && confirmPassword.getValue() !="" && email.getValue() != ""){
                if (verifyPassword(password.getValue(), confirmPassword.getValue())) {
                    userService.add(new User(username.getValue(), password.getValue(), confirmPassword.getValue(), email.getValue()));
                    userDetailsService.loadUserByUsername(username.getValue());
                    VaadinSession currentSession = getSession();
                    loginView.login(username.getValue(), password.getValue(), currentSession);
                } else {
                    Notification.show("Passwords don't match!", Notification.Type.ERROR_MESSAGE);
                }
            }else {
                Notification.show("All fields are mandatory!", Notification.Type.ERROR_MESSAGE);
            }
        });

        register.setClickShortcut(ShortcutAction.KeyCode.ENTER);
    }

    public boolean verifyPassword(String password, String confirmPassword) {
        if (password.equals(confirmPassword)) {
            return true;
        }
        return false;
    }
}
