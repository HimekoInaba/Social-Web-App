package kz.vaadin.view;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import kz.vaadin.model.User;
import kz.vaadin.repository.UsersRepository;
import kz.vaadin.ui.RootUI;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.spring.security.VaadinSecurity;

import java.util.List;

@SpringView(name = UserListView.VIEW_NAME)
public class UserListView extends VerticalLayout implements View {

    public static final String VIEW_NAME = "/userlist";

    @Autowired
    UsersRepository usersRepository;

    @Autowired
    VaadinSecurity vaadinSecurity;

    public void initializeForms(){
        Label label = new Label("List of all users");
        Button logout = new Button("Logout");
        List<User> users;
        users = usersRepository.findAll();
        Grid<User> grid = new Grid<>();

        grid.setItems(users);
        grid.addColumn(User::getId).setCaption("Id");
        grid.addColumn(User::getUsername).setCaption("Username");
        grid.addColumn(User::getEmail).setCaption("Email");

        addComponents(label, grid, logout);
        setComponentAlignment(label, Alignment.MIDDLE_CENTER);
        setComponentAlignment(grid, Alignment.MIDDLE_CENTER);
        setComponentAlignment(logout, Alignment.TOP_RIGHT);

        label.addStyleName(ValoTheme.LABEL_H1);

        logout.addClickListener(click -> {
            getUI().getNavigator().navigateTo(RootUI.LOGINVIEW);
            RootUI.getCurrent().getSession().close();
        });
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        if(vaadinSecurity.isAuthenticated()){
            initializeForms();
        }else{
            getUI().getNavigator().navigateTo(RootUI.NOTLOGGEDINVEW);
        }
    }
}
