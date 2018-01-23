package kz.vaadin.ui;

import javax.servlet.annotation.WebListener;
import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Push;
import com.vaadin.navigator.Navigator;
import com.vaadin.shared.communication.PushMode;
import com.vaadin.shared.ui.ui.Transport;
import com.vaadin.spring.navigator.SpringViewProvider;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import kz.vaadin.model.User;
import kz.vaadin.service.UserService;
import kz.vaadin.view.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.web.context.ContextLoaderListener;

import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.EnableVaadin;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.server.SpringVaadinServlet;
import org.vaadin.spring.security.VaadinSecurity;

/**
 * This ui is the application entry point. A ui may either represent a browser window
 * (or tab) or some part of an HTML page where a Vaadin application is embedded.
 * <p>
 * The ui is initialized using {@link #init(VaadinRequest)}. This method is intended to be
 * overridden to add component to the user interface and initialize non-component functionality.
 */
@Theme("mytheme")
@SpringUI(path = "/")
@SuppressWarnings("serial")
@Push(value = PushMode.AUTOMATIC, transport = Transport.LONG_POLLING)
public class RootUI extends UI implements LoginInterface{

    @WebServlet(value = "/*", asyncSupported = true)
    public static class Servlet extends SpringVaadinServlet {
    }

    @WebListener
    public static class MyContextLoaderListener extends ContextLoaderListener {
    }

    @Configuration
    @EnableVaadin
    public static class MyConfiguration {
    }

    @Autowired
    SpringViewProvider viewProvider;

    public final static String REGISTRATIONVIEW =  RegistrationView.VIEW_NAME;
    public final static String USERPROFILEVIEW =  UserProfileView.VIEW_NAME;
    public final static String LOGINVIEW =  LoginView.VIEW_NAME;
    public final static String USERLISTVIEW = UserListView.VIEW_NAME;
    public final static String NOTLOGGEDINVEW = NotLoggedErrorView.VIEW_NAME;

    @Autowired
    VaadinSecurity vaadinSecurity;

    @Autowired
    UserService userService;

    private Navigator navigator;

    User user;

    @Autowired
    private ApplicationContext appContext;

    final VerticalLayout layout = new VerticalLayout();

    @Override
    protected void init(VaadinRequest request) {

        System.out.println("AppContext: " + appContext.getDisplayName() + "  " + appContext.getStartupDate());
        if (vaadinSecurity.isAuthenticated()) {
            showMainScreen();
        }else{
            setContent(appContext.getBean(LoginView.class));
        }
    }

    @Override
    public void login(Authentication authentication) {
        if(authentication.isAuthenticated()) {
            access(this::showMainScreen);
        }
    }

    private void showMainScreen() {
        createNavigator();
        layout.setMargin(true);
        layout.setWidth("80%");
        setSizeUndefined();
        setContent(layout);

        Label welcome = new Label("You are succefully loged in, welcome to the Main Screen");
        Button profile = new Button("Your profile");
        Button userlist = new Button("List of all users");
        Button logout = new Button("Logout");

        layout.addComponents(welcome, profile, userlist, logout);

        welcome.addStyleName(ValoTheme.LABEL_H1);

        profile.addClickListener(click -> {
            user = (User) getUI().getSession().getAttribute("user");
            getUI().getNavigator().navigateTo(RootUI.USERPROFILEVIEW + "/" + user.getId());
        });

        userlist.addClickListener(click -> getUI().getNavigator().navigateTo(RootUI.USERLISTVIEW));

        logout.addClickListener(click -> {
            //setContent(appContext.getBean(LoginView.class));
            vaadinSecurity.logout();
            System.out.println(vaadinSecurity.isAuthenticated());
            RootUI.getCurrent().getSession().close();
            System.out.println(vaadinSecurity.isAuthenticated());
            this.close();
            this.detach();
            // Redirect from the page
            getUI().getPage().setLocation("/");
            // Close the VaadinSession
            getSession().close();
        });
    }

     public void registerNavigation(){
        setContent(appContext.getBean(RegistrationView.class));
    }

    private void createNavigator(){

        // Create a navigator to control the views
        navigator = new Navigator(this, this);

        // Create and register the views
        navigator.addProvider(viewProvider);
        setNavigator(navigator);
    }
}
