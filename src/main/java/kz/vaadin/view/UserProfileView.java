package kz.vaadin.view;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FileResource;
import com.vaadin.server.Page;
import com.vaadin.server.StreamResource;
import com.vaadin.server.VaadinService;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.*;
import com.vaadin.ui.Upload.*;
import com.vaadin.ui.themes.ValoTheme;
import kz.vaadin.model.User;
import kz.vaadin.service.UserServiceImpl;
import kz.vaadin.ui.RootUI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.userdetails.UserDetailsService;
import sun.misc.IOUtils;

import javax.imageio.ImageIO;
import javax.persistence.*;
import javax.xml.transform.stream.StreamSource;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.sql.Blob;
import java.sql.Date;
import java.text.DateFormat;

@Secured({"ROLE_USER", "ROLE_ADMIN"})
@SpringView(name = UserProfileView.VIEW_NAME)
public class UserProfileView extends VerticalLayout implements View{

    public static final String VIEW_NAME = "/profile";

    @Autowired
    UserServiceImpl userService;

    @Autowired
    UserDetailsService userDetailsService;

    @Autowired
    RootUI rootUI;

    private User user;
    private long id;
    private Image avatar;

    public void initializeForms(){

        user = userService.findById(id);

        Button logout = new Button("Logout");
        Button userList = new Button("List of all users");
        Label greetingField = new Label("Welcome to your profile, " + user.getUsername());
        Label currentSessionUsername = new Label(user.getUsername());
        Label email = new Label("Email: " + user.getEmail());
        Button changeAvatar = new Button("Change avatar");

        showAvatar();

        addComponents(greetingField, avatar, email, changeAvatar, currentSessionUsername, logout, userList);
        
        setComponentAlignment(greetingField, Alignment.TOP_LEFT);
        setComponentAlignment(avatar, Alignment.TOP_LEFT);
        setComponentAlignment(email, Alignment.TOP_LEFT);
        setComponentAlignment(changeAvatar, Alignment.TOP_LEFT);
        setComponentAlignment(currentSessionUsername, Alignment.TOP_RIGHT);
        setComponentAlignment(logout, Alignment.TOP_RIGHT);
        setComponentAlignment(userList, Alignment.BOTTOM_CENTER);

        logout.setHeight("100");
        greetingField.addStyleName(ValoTheme.LABEL_H1);
        email.addStyleName(ValoTheme.LABEL_H2);
        currentSessionUsername.addStyleName(ValoTheme.LABEL_H3);

        logout.addClickListener(click -> rootUI.logout());
        userList.addClickListener(click -> rootUI.navigateToUserlist());
        changeAvatar.addClickListener(click -> uploadAvatar());
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        if(event.getParameters() != null){
            // split at "/", add each part as a label
            String[] msgs = event.getParameters().split("/");
            for (String msg : msgs) {
                id = (long) Integer.parseInt(msg);
            }
        }
        initializeForms();
    }

    void showAvatar(){
        BufferedImage bufferedImage = userService.getAvatar(user);

        //FileResource resource = new FileResource(new File("C:\\Users\\s.tusupbekov\\IdeaProjects\\Vaadin-Spring-integration-web-application-949c95fdec9ed1b5458c008452842391b9fb3f92\\src\\main\\resources\\avatars\\default_avatar.jpg\\"));

        //avatar = new Image("aa",);
        avatar.setVisible(true);
    }


    void uploadAvatar() {
        class UploadBox extends CustomComponent implements Receiver, ProgressListener, FailedListener, SucceededListener {

            ProgressBar progress = new ProgressBar(0.0f);
            public File file;
            final String PATH = "C:\\Users\\s.tusupbekov\\IdeaProjects\\Vaadin-Spring-integration-web-application-949c95fdec9ed1b5458c008452842391b9fb3f92\\src\\main\\resources\\avatars\\";

            // Show uploaded file in this placeholder
             Image image = new Image();

            public UploadBox() {
                // Create the upload component and handle all its events
                Upload upload = new Upload("Upload the image here", null);
                upload.setReceiver(this);
                upload.addProgressListener(this);
                upload.addFailedListener(this);
                upload.addSucceededListener(this);

                // Put the upload and image display in a panel
                Panel panel = new Panel("Image Storage");
                panel.setWidth("200px");
                VerticalLayout panelContent = new VerticalLayout();
                panelContent.setSpacing(true);
                panel.setContent(panelContent);
                panelContent.addComponent(upload);
                panelContent.addComponent(progress);

                progress.setVisible(false);
                image.setVisible(false);

                setCompositionRoot(panel);
            }

            public OutputStream receiveUpload(String filename, String mimeType) {
                FileOutputStream fos = null; // Stream to write to
                try {
                    // Open the file for writing.
                    file = new File(PATH + filename);
                    fos = new FileOutputStream(file);
                } catch (final java.io.FileNotFoundException e) {
                    new Notification("Could not open file<br/>",
                            e.getMessage(),
                            Notification.Type.ERROR_MESSAGE)
                            .show(Page.getCurrent());
                    return null;
                }
                return fos;
            }

            @Override
            public void updateProgress(long readBytes, long contentLength) {
                progress.setVisible(true);
                if (contentLength == -1)
                    progress.setIndeterminate(true);
                else {
                    progress.setIndeterminate(false);
                    progress.setValue(((float)readBytes) /
                            ((float)contentLength));
                }
            }

            public void uploadSucceeded(SucceededEvent event) {
                image.setVisible(true);
                image.setSource(new FileResource(file));
                uploadToDatabase();
                image.markAsDirty();
                addComponent(image);
                setComponentAlignment(image, Alignment.TOP_LEFT);
            }

            @Override
            public void uploadFailed(FailedEvent event) {
                Notification.show("Upload failed",
                        Notification.Type.ERROR_MESSAGE);
            }

            public void uploadToDatabase(){
                byte[] bFile = new byte[(int) file.length()];
                try {
                    FileInputStream fileInputStream = new FileInputStream(file);
                    fileInputStream.read(bFile);
                    bFile = Files.readAllBytes((file).toPath());
                    Blob blob = new javax.sql.rowset.serial.SerialBlob(bFile);
                    User user = (User) getUI().getSession().getAttribute("user");
                    user.setAvatar(blob);

                    System.out.println(blob + " Avatar");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        UploadBox uploadbox = new UploadBox();
        addComponent(uploadbox);
    }
}
