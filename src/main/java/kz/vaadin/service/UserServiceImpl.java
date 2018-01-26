package kz.vaadin.service;

import kz.vaadin.model.Roles;
import kz.vaadin.model.User;
import kz.vaadin.repository.RolesRepository;
import kz.vaadin.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import javax.sql.rowset.serial.SerialBlob;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.sql.Blob;
import java.sql.SQLException;
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

    public void add(User user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        setDefaultAvatar(user);
        Set<Roles> roles = new HashSet<>();
        roles.add(rolesRepository.getOne(2L));
        user.setRoles(roles);
        userRepository.save(user);
    }

    public BufferedImage getAvatar(User user){
        Blob blob = user.getAvatar();
        int blobLength;
        BufferedImage img;
        byte[] blobAsBytes;

        try {
            blobLength = (int) blob.length();
            blobAsBytes = blob.getBytes(1, blobLength);
            img = ImageIO.read(new ByteArrayInputStream(blobAsBytes));

            return img;
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void setDefaultAvatar(User user){
        final String FILENAME = "default_avatar.jpg";
        final String PATH = "C:\\Users\\s.tusupbekov\\IdeaProjects\\Vaadin-Spring-integration-web-application-949c95fdec9ed1b5458c008452842391b9fb3f92\\src\\main\\resources\\avatars\\";
        File file = new File(PATH + FILENAME);

        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            byte[] bFile = new byte[(int) file.length()];
            fileInputStream.read(bFile);
            bFile = Files.readAllBytes((file).toPath());
            Blob blob = new SerialBlob(bFile);
            user.setAvatar(blob);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
