package kz.vaadin.client.rest;

public class RestUser {

    private final long id;
    private final String username;
    private final String password;
    private final String confirmPassword;
    private final String email;

    public RestUser(long id, String username, String password, String confirmPassword, String email) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.confirmPassword = confirmPassword;
        this.email = email;
    }

    public long getId(){
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public String getEmail() {
        return email;
    }
}
