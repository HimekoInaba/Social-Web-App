package kz.vaadin.view;

import com.vaadin.navigator.View;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import kz.vaadin.ui.RootUI;

@SpringView(name = NotLoggedErrorView.VIEW_NAME)
public class NotLoggedErrorView extends VerticalLayout implements View {

    public static final String VIEW_NAME = "/notlogged";

    public NotLoggedErrorView(){
        Label message = new Label("Please log in to see this page");
        Button login = new Button("Return to login page");
        addComponents(message, login);
        message.addStyleName(ValoTheme.LABEL_H1);

        login.addClickListener(clickEvent -> getUI().getNavigator().navigateTo(RootUI.LOGINVIEW));
    }
}
