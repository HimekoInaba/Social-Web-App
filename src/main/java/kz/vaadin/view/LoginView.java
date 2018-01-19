package kz.vaadin.view;

import com.vaadin.event.ShortcutAction;
import com.vaadin.external.org.slf4j.LoggerFactory;
import com.vaadin.navigator.View;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import kz.vaadin.model.User;
import kz.vaadin.service.UserServiceImpl;
import kz.vaadin.ui.RootUI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.vaadin.spring.security.VaadinSecurity;


@SpringView(name = LoginView.VIEW_NAME)
public class LoginView extends VerticalLayout implements View {

    @Autowired
    UserServiceImpl userService;

    @Autowired
    VaadinSecurity vaadinSecurity;

    public static final String VIEW_NAME = "/login";

    User user;

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
                login(username.getValue(), password.getValue());
                user = userService.findByUsername(username.getValue());
                if (user == null) {
                    Notification.show("Incorrect username or password", Notification.Type.ERROR_MESSAGE);
                    getUI().getNavigator().navigateTo(RootUI.LOGINVIEW);
                } else {
                    getSession().setAttribute("user", user);
                    getUI().getNavigator().navigateTo(RootUI.USERPROFILEVIEW + "/" + user.getId());
                }
            }else{
                Notification.show("Empty username or password!", Notification.Type.ERROR_MESSAGE);
            }
        });

        register.addClickListener(click -> getUI().getNavigator().navigateTo(RootUI.REGISTRATIONVIEW));

        login.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        register.setClickShortcut(ShortcutAction.KeyCode.ENTER);
    }

    public void login(String username, String password) {
        try {
             vaadinSecurity.login(username, password);
        } catch (AuthenticationException ex){
            loginFailedLabel.setValue(String.format("Login failed: %s", ex.getMessage()));
            loginFailedLabel.setVisible(true);
        } catch (Exception ex) {
            Notification.show("An unexpected error occurred", ex.getMessage(), Notification.Type.ERROR_MESSAGE);
            LoggerFactory.getLogger(getClass()).error("Unexpected error while logging in", ex);
        } finally {
            login.setEnabled(true);
        }
    }
}
