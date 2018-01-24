package kz.vaadin.view;

import com.vaadin.event.ShortcutAction;
import com.vaadin.external.org.slf4j.LoggerFactory;
import com.vaadin.navigator.View;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import kz.vaadin.model.User;
import kz.vaadin.service.UserServiceImpl;
import kz.vaadin.ui.LoginInterface;
import kz.vaadin.ui.RootUI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import org.vaadin.spring.annotation.PrototypeScope;
import org.vaadin.spring.security.VaadinSecurity;


@PrototypeScope
@Component
public class LoginView extends VerticalLayout implements View {

    @Autowired
    UserServiceImpl userService;

    @Autowired
    VaadinSecurity vaadinSecurity;

    @Autowired
    private ApplicationContext appContext;

    @Autowired
    private RootUI rootUI;

    private User user;

    private Label loginFailedLabel;
    private Button login;

    public LoginView() {

        Label label = new Label("Enter username and password below to log in:");
        label.addStyleName(ValoTheme.LABEL_H1);
        TextField username = new TextField("Username");
        PasswordField password = new PasswordField("Password");
        login = new Button("Login");
        Button register = new Button("Register");
        loginFailedLabel = new Label();

        addComponents(label, username, password, login, register, loginFailedLabel);

        loginFailedLabel.setSizeUndefined();
        loginFailedLabel.addStyleName(ValoTheme.LABEL_FAILURE);
        loginFailedLabel.setVisible(false);

        setComponentAlignment(label, Alignment.MIDDLE_CENTER);
        setComponentAlignment(username, Alignment.MIDDLE_CENTER);
        setComponentAlignment(password, Alignment.MIDDLE_CENTER);
        setComponentAlignment(login, Alignment.MIDDLE_CENTER);
        setComponentAlignment(register, Alignment.MIDDLE_CENTER);
        setComponentAlignment(loginFailedLabel, Alignment.BOTTOM_CENTER);

        login.addClickListener(click -> {
            if(username.getValue() != "" && password.getValue() != "") {
                login(username.getValue(), password.getValue(), null);
            }else{
                Notification.show("Empty username or password!", Notification.Type.ERROR_MESSAGE);
            }
        });

        register.addClickListener(click -> rootUI.registerNavigation());

        login.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        register.setClickShortcut(ShortcutAction.KeyCode.ENTER);
    }

    public void login(String username, String password, VaadinSession vaadinSession) {
        try {
             final Authentication authentication = vaadinSecurity.login(username, password);
             user = userService.findByUsername(username);

             if(RootUI.getCurrent().getSession() == null)
                 RootUI.getCurrent().setSession(vaadinSession);

             RootUI.getCurrent().getSession().setAttribute("user", user);
             LoginInterface loginInterface = appContext.getBean(LoginInterface.class);
             loginInterface.login(authentication);
        } catch (AuthenticationException ex){
            loginFailedLabel.setValue(String.format("Login failed: %s", ex.getMessage()));
            loginFailedLabel.setVisible(true);
            Notification.show("Incorrect username or password", Notification.Type.ERROR_MESSAGE);
        } catch (Exception ex) {
            Notification.show("An unexpected error occurred", ex.getMessage(), Notification.Type.ERROR_MESSAGE);
            LoggerFactory.getLogger(getClass()).error("Unexpected error while logging in", ex);
        } finally {
            login.setEnabled(true);
        }
    }
}
