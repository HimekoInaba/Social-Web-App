package kz.vaadin.view;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import kz.vaadin.model.User;
import kz.vaadin.repository.UsersRepository;
import kz.vaadin.service.UserService;
import kz.vaadin.ui.RootUI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;

@Secured({"ROLE_ADMIN"})
@SpringView(name = EditUserProfileView.VIEW_NAME)
public class EditUserProfileView extends VerticalLayout implements View {

    public static final String VIEW_NAME = "/editprofile";
    private long id;
    private User user;

    @Autowired
    UserService userService;

    @Autowired
    UsersRepository usersRepository;

    public void initializeForms(){

        user = userService.findById(id);

        Label welcomeLabel = new Label("Edit " + user.getUsername() + "'s" + " profile");
        TextField username = new TextField("Username", user.getUsername());
        TextField email = new TextField("Email", user.getEmail());
        Button save = new Button("Save changes");
        Button list = new Button("Back to Userlist Page");

        addComponents(welcomeLabel, username, email, save, list);

        welcomeLabel.addStyleName(ValoTheme.LABEL_H1);

        setComponentAlignment(welcomeLabel, Alignment.MIDDLE_CENTER);
        setComponentAlignment(username, Alignment.MIDDLE_CENTER);
        setComponentAlignment(email, Alignment.MIDDLE_CENTER);
        setComponentAlignment(save, Alignment.MIDDLE_CENTER);
        setComponentAlignment(list, Alignment.MIDDLE_CENTER);

        save.addClickListener(event -> {
            user.setUsername(username.getValue());
            user.setEmail(email.getValue());
            String exception  = userService.saveUser(user);
            if(exception != null)
                Notification.show(exception, Notification.Type.ERROR_MESSAGE);
            else
                Notification.show("Changes were successfully set", Notification.Type.ASSISTIVE_NOTIFICATION);
        });

        list.addClickListener(event -> RootUI.getCurrent().getNavigator().navigateTo(RootUI.USERLISTVIEW));
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        if(event.getParameters() != null){
            String[] msgs = event.getParameters().split("/");
            for (String msg : msgs) {
                id = (long) Integer.parseInt(msg);
            }
        }
        initializeForms();
    }
}
