package kz.vaadin.view;

import com.vaadin.data.Binder;
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

import javax.persistence.Column;
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
        TextField username = new TextField("");
        TextField email = new TextField("");

        Binder<User> binder = grid.getEditor().getBinder();

        Binder.Binding<User, String> usernameBinder = binder.forField(username)
                .bind(User::getUsername, User::setUsername);


        Binder.Binding<User, String> emailBinder = binder.forField(email)
                .bind(User::getEmail, User::setEmail);

        grid.setWidth("50%");
        grid.getEditor().setEnabled(true);
        grid.setItems(users);
        grid.addColumn(User::getId).setCaption("Id");
        grid.addColumn(User::getUsername).setCaption("Username").setId("username");
        grid.addColumn(User::getEmail).setCaption("Email").setId("email");
        grid.addColumn(person -> "Remove",
                new ButtonRenderer(clickEvent -> {
                    User user = (User) clickEvent.getItem();
                    usersRepository.delete(user);
                    users.remove(clickEvent.getItem());
                    grid.setItems(users);
                })).setCaption("Delete");
        grid.addColumn(person -> "Save",
                new ButtonRenderer<>(clickEvent ->{
                    User user = clickEvent.getItem();
                    usersRepository.save(user);
                    grid.setItems(users);
                    grid.clearSortOrder();
                })).setCaption("Save changes");
        grid.addColumn(person -> "Go to profile",
                    new ButtonRenderer<>(clickEvent ->{
                        User user = clickEvent.getItem();
                        getUI().getNavigator().navigateTo(RootUI.USERPROFILEVIEW + "/" + user.getId());
                    })).setCaption("Profile");

        addComponents(label, grid, mainView, logout);
        label.addStyleName(ValoTheme.LABEL_H1);
        setComponentAlignment(label, Alignment.MIDDLE_CENTER);
        setComponentAlignment(grid, Alignment.MIDDLE_CENTER);
        setComponentAlignment(mainView, Alignment.MIDDLE_CENTER);
        setComponentAlignment(logout, Alignment.TOP_RIGHT);

        grid.getColumn("username").setEditorBinding(usernameBinder);
        grid.getColumn("email").setEditorBinding(emailBinder);

        mainView.addClickListener(click -> RootUI.getCurrent().getNavigator().navigateTo("/#!/"));
        logout.addClickListener(click -> rootUI.logout());
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        initializeForms();
    }
}