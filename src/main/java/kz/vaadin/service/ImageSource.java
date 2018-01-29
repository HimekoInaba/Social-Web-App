package kz.vaadin.service;

import kz.vaadin.model.User;
import kz.vaadin.ui.RootUI;
import org.springframework.beans.factory.annotation.Autowired;

import javax.imageio.ImageIO;
import javax.xml.transform.stream.StreamSource;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ImageSource extends StreamSource {

    @Autowired
    UserService userService;

    ByteArrayOutputStream imagebuffer = null;

    public InputStream getStream () {

        User user = (User) RootUI.getCurrent().getSession().getAttribute("user");
        BufferedImage image = userService.getAvatar(user);

        try {
            imagebuffer = new ByteArrayOutputStream();
            ImageIO.write(image, "png", imagebuffer);

            return new ByteArrayInputStream(
                    imagebuffer.toByteArray());
        } catch (IOException e) {
            return null;
        }
    }
}
