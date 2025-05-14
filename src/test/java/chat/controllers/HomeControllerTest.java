package chat.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationExtension;

import com.chat.client.Client;
import com.chat.controller.HomeController;
import com.chat.models.UserProfile;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

@ExtendWith(MockitoExtension.class)
@ExtendWith(ApplicationExtension.class)
public class HomeControllerTest {

    private HomeController homeController;
    private Client client;
    
    @BeforeEach
    public void setUp() throws IOException {
        client = mock(Client.class);
        UserProfile userProfile = new UserProfile(1, "testUser", "Test User", "test bio", "test status", new File("test_picture.jpg"));
        when(client.getUserProfile()).thenReturn(userProfile);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/home.fxml"));
        VBox root = loader.load();
        homeController = loader.getController();
        homeController.setClient(client);

        Platform.runLater(() -> {
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
        });
    }

    
}
