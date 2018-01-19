package kz.vaadin.ui;

import javax.servlet.annotation.WebListener;
import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Push;
import com.vaadin.navigator.Navigator;
import com.vaadin.shared.communication.PushMode;
import com.vaadin.shared.ui.ui.Transport;
import com.vaadin.spring.navigator.SpringViewProvider;
import com.vaadin.ui.*;
import kz.vaadin.model.User;
import kz.vaadin.view.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
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
@SpringUI
@SuppressWarnings("serial")
@Push(value = PushMode.AUTOMATIC, transport = Transport.LONG_POLLING)
public class RootUI extends UI {

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

    @Override
    protected void init(VaadinRequest request) {

        // Create a navigator to control the views
        Navigator navigator = new Navigator(this, this);

        // Create and register the views
        navigator.addProvider(viewProvider);
        setNavigator(navigator);

        final VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        layout.setWidth("80%");
        setContent(layout);

        if (vaadinSecurity.isAuthenticated()) {
            User user = (User) getSession().getAttribute("user");
            getUI().getNavigator().navigateTo(RootUI.USERPROFILEVIEW + "/" + user.getId());
        } else {
            getUI().getNavigator().navigateTo(RootUI.LOGINVIEW);
        }
    }
}
