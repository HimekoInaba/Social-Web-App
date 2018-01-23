package kz.vaadin.service;

import kz.vaadin.model.User;

public interface UserService {
    User findByUsername(String username);
    User findById(long id);
    void add(User user);
}
