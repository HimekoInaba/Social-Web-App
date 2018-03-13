package boot.rest.restboot;

import java.awt.image.BufferedImage;

public interface UserService {
    User findByUsername(String username);
    User findById(long id);
    String add(User user);
    void setDefaultAvatar(User user);
    BufferedImage getAvatar(User user);
    String saveUser(User user);
}
