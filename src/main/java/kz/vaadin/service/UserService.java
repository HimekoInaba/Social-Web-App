package kz.vaadin.service;

import kz.vaadin.model.User;

public interface UserService {
    public User findByUsername(String username);
    public User findById(long id);
    public void add(User user);
}
