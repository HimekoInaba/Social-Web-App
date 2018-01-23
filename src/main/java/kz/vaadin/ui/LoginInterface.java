package kz.vaadin.ui;

import org.springframework.security.core.Authentication;

public interface LoginInterface {
    void login(Authentication authentication);
}
