package chat.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.chat.client.Client;
import com.chat.controller.FriendRequestsController;
import com.chat.models.UserProfile;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

@ExtendWith(MockitoExtension.class)
public class FriendRequestsControllerTest {

    private FriendRequestsController friendRequestsController;
    private Client client;

    @BeforeEach
    public void setUp() {
        client = mock(Client.class);
        friendRequestsController = new FriendRequestsController();
        friendRequestsController.setClient(client);
    }

}