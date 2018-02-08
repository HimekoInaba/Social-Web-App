package kz.vaadin.view;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.*;
import com.vaadin.ui.renderers.ButtonRenderer;
import com.vaadin.ui.themes.ValoTheme;
import kz.vaadin.model.User;
import kz.vaadin.repository.RolesRepository;
import kz.vaadin.repository.UsersRepository;
import kz.vaadin.ui.RootUI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;

import java.util.List;

@Secured({"ROLE_ADMIN"})
@SpringView(name = UserListView.VIEW_NAME)
public class UserListView extends VerticalLayout implements View {

    public static final String VIEW_NAME = "/userlist";

    @Autowired
    UsersRepository usersRepository;

    @Autowired
    RolesRepository rolesRepository;

    @Autowired
    RootUI rootUI;

    public void initializeForms(){

        Label label = new Label("List of all users");
        Button logout = new Button("Logout");
        Button mainView = new Button("Back to Main Page");
        List<User> users = usersRepository.findAll();
        Grid<User> grid = new Grid<>();

        grid.setWidth("50%");
        grid.getEditor().setEnabled(true);
        grid.setItems(users);
        grid.addColumn(User::getId).setCaption("Id");
        grid.addComponentColumn(myBean -> {
            User user = myBean;
            Button username = new Button(user.getUsername());
            username.addStyleName(ValoTheme.BUTTON_LINK);
            username.addClickListener(event ->
                    RootUI.getCurrent().getNavigator().navigateTo(RootUI.EDITUSERPROFILE + "/" + user.getId()));
            return username;
        }).setCaption("Username");
        grid.addColumn(User::getEmail).setCaption("Email").setId("email");
        grid.addColumn(user -> "Remove",
                new ButtonRenderer(clickEvent -> {
                    User user = (User) clickEvent.getItem();
                    usersRepository.delete(user);
                    users.remove(clickEvent.getItem());
                    grid.setItems(users);
                })).setCaption("Delete");

        addComponents(label, grid, mainView, logout);
        label.addStyleName(ValoTheme.LABEL_H1);
        setComponentAlignment(label, Alignment.MIDDLE_CENTER);
        setComponentAlignment(grid, Alignment.MIDDLE_CENTER);
        setComponentAlignment(mainView, Alignment.MIDDLE_CENTER);
        setComponentAlignment(logout, Alignment.TOP_RIGHT);

        mainView.addClickListener(click -> getUI().getPage().setLocation("/home/"));
        logout.addClickListener(click -> rootUI.logout());
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        initializeForms();
    }
}