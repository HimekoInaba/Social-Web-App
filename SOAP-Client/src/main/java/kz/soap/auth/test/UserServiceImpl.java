package kz.soap.auth.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UsersRepository userRepository;

    @Autowired
    RolesRepository rolesRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User findById(long id) {
        return userRepository.findById(id);
    }

    public String add(User user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        setDefaultAvatar(user);
        Set<Roles> roles = new HashSet<>();
        roles.add(rolesRepository.getOne(2L));
        user.setRoles(roles);
        return saveUser(user);
    }

    public String saveUser(User user){
        try {
            userRepository.save(user);
        }catch (DataIntegrityViolationException ex){
            return "Entered username is already used!";
        }
        return null;
    }

    public BufferedImage getAvatar(User user){
        BufferedImage img;
        byte[] blobAsBytes = user.getAvatar();

        try {
            img = ImageIO.read(new ByteArrayInputStream(blobAsBytes));
            return img;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }


    public void setDefaultAvatar(User user){
        final String FILENAME = "default_avatar.png";
        final String PATH = "C:\\Users\\s.tusupbekov\\IdeaProjects\\Vaadin-Spring-integration-web-application-949c95fdec9ed1b5458c008452842391b9fb3f92\\src\\main\\resources\\avatars\\";
        File file = new File(PATH + FILENAME);

        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            byte[] bFile = new byte[(int) file.length()];
            fileInputStream.read(bFile);
            bFile = Files.readAllBytes((file).toPath());
            user.setAvatar(bFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
