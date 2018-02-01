package kz.vaadin.view;

import com.vaadin.server.StreamResource;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.Page;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.*;
import com.vaadin.ui.Button;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Upload.*;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.ui.Upload.FailedListener;
import com.vaadin.ui.Upload.ProgressListener;
import com.vaadin.ui.Upload.Receiver;
import kz.vaadin.model.User;
import kz.vaadin.service.UserServiceImpl;
import kz.vaadin.ui.RootUI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.userdetails.UserDetailsService;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;


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

        greetingField.addStyleName(ValoTheme.LABEL_H1);
        email.addStyleName(ValoTheme.LABEL_H2);
        currentSessionUsername.addStyleName(ValoTheme.LABEL_H3);

        logout.addClickListener(click -> rootUI.logout());
        userList.addClickListener(click -> rootUI.navigateToUserlist());
        changeAvatar.addClickListener(click -> {
            changeAvatar.setVisible(false);
            uploadAvatar();
            changeAvatar.setVisible(true);
        });
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

    private void showAvatar(){
        StreamResource imageResource = createStreamResource();
        imageResource.setCacheTime(0);

        avatar = new Image(null, imageResource);
        avatar.markAsDirty();
        avatar.setVisible(true);
    }

    private StreamResource createStreamResource() {
        return new StreamResource(new StreamResource.StreamSource() {
            public InputStream getStream() {

                BufferedImage bi = userService.getAvatar(user);
                try {
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    ImageIO.write(bi, "png", bos);
                    return new ByteArrayInputStream(bos.toByteArray());
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }, "dateImage.png");
    }


    void uploadAvatar() {
        class UploadBox extends CustomComponent implements Receiver, ProgressListener, FailedListener, SucceededListener {

            ProgressBar progress = new ProgressBar(0.0f);
            public File file;
            final String PATH = "C:\\Users\\s.tusupbekov\\IdeaProjects\\Vaadin-Spring-integration-web-application-949c95fdec9ed1b5458c008452842391b9fb3f92\\src\\main\\resources\\avatars\\";
            Upload upload;

            public UploadBox() {
                // Create the upload component and handle all its events
                upload = new Upload("Upload the image here", null);
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
                setCompositionRoot(panel);
            }

            public OutputStream receiveUpload(String filename, String mimeType) {

                FileOutputStream fos = null;
                file = new File(PATH + filename);
                try {
                    fos = new FileOutputStream(file);

                } catch (final java.io.FileNotFoundException e) {
                    new Notification("Could not open file<br/>",
                            e.getMessage(),
                            Notification.Type.ERROR_MESSAGE)
                            .show(Page.getCurrent());
                    return null;
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return fos;
            }

            /*public boolean uploadStarted(String contentType) {

                ArrayList<String> allowedMimeTypes = new ArrayList<>();
                allowedMimeTypes.add("image/jpg");
                allowedMimeTypes.add("image/png");

                boolean allowed = false;
                for(int i=0;i<allowedMimeTypes.size();i++){
                    if(contentType.equalsIgnoreCase(allowedMimeTypes.get(i))){
                        allowed = true;
                        break;
                    }
                }

                return allowed;
            }*/

            public void uploadToDB(){
                FileInputStream fileInputStream;
                try {
                    fileInputStream = new FileInputStream(file);
                    byte[] bFile = new byte[(int) file.length()];
                    fileInputStream.read(bFile);
                    bFile = Files.readAllBytes((file).toPath());
                    user.setAvatar(bFile);
                    userService.saveUser(user);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void updateProgress(long readBytes, long contentLength) {
                uploadToDB();
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
                progress.setVisible(false);
                avatar.markAsDirty();
                getUI().getPage().reload();
            }

            @Override
            public void uploadFailed(FailedEvent event) {
                Notification.show("Upload failed",
                        Notification.Type.ERROR_MESSAGE);
            }
        }

        UploadBox uploadbox = new UploadBox();
        addComponent(uploadbox);
    }
}
