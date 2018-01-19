package kz.vaadin.view;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import kz.vaadin.model.User;
import kz.vaadin.service.UserServiceImpl;
import kz.vaadin.ui.RootUI;
import org.springframework.beans.factory.annotation.Autowired;
import com.vaadin.ui.Label;
import org.springframework.security.access.annotation.Secured;
import org.vaadin.spring.security.VaadinSecurity;

@SpringView(name = UserProfileView.VIEW_NAME)
public class UserProfileView extends VerticalLayout implements View {

    public static final String VIEW_NAME = "/profile";

    @Autowired
    UserServiceImpl userService;

    @Autowired
    VaadinSecurity vaadinSecurity;

    private User user;
    private long id;

    public UserProfileView(){
    }

    public void initializeForms(){
        user = userService.findById(id);

        Button logout = new Button("Logout");
        Button userList = new Button("List of all users");
        Label greetingField = new Label("Welcome to your profile, " + user.getUsername());
        Label currentSessionUsername = new Label(user.getUsername());
        Label email = new Label("Email: " + user.getEmail());

        addComponents(greetingField, email, currentSessionUsername, logout, userList);

        setComponentAlignment(greetingField, Alignment.TOP_LEFT);
        setComponentAlignment(email, Alignment.TOP_LEFT);
        setComponentAlignment(currentSessionUsername, Alignment.TOP_RIGHT);
        setComponentAlignment(logout, Alignment.TOP_RIGHT);
        setComponentAlignment(userList, Alignment.BOTTOM_CENTER);

        greetingField.addStyleName(ValoTheme.LABEL_H1);
        email.addStyleName(ValoTheme.LABEL_H2);
        currentSessionUsername.addStyleName(ValoTheme.LABEL_H3);

        logout.addClickListener(click ->{
            getUI().getNavigator().navigateTo(RootUI.LOGINVIEW);
            RootUI.getCurrent().getSession().close();
        });

        userList.addClickListener(click -> getUI().getNavigator().navigateTo(RootUI.USERLISTVIEW));
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        if(event.getParameters() != null){
            // split at "/", add each part as a label
            String[] msgs = event.getParameters().split("/");
            for (String msg : msgs) {
                id = (long) Integer.parseInt(msg);
            }
        }
        if(vaadinSecurity.isAuthenticated()){
            initializeForms();
        }else{
            getUI().getNavigator().navigateTo(RootUI.NOTLOGGEDINVEW);
        }
    }
}
