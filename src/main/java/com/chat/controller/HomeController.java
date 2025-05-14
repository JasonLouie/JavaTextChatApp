package com.chat.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.chat.client.Client;
import com.chat.models.UserProfile;
import java.io.IOException;

public class HomeController {
    @FXML
    private VBox contentArea;

    private Client client;

    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    public void setClient(Client client) {
        this.client = client;
    }

    public void init() {
        new Thread(() -> {
            try {
                Platform.runLater(this::showHome);
            } catch (Exception e) {
                logger.error("Error initializing home controller", e);
                Platform.runLater(() -> displayError("Error initializing home controller: " + e.getMessage()));
            }
        }).start();
    }

    @FXML
    public VBox getContentArea() {
        return contentArea;
    }

    @FXML
    public void showHome() {
        loadUserProfile(client.getUserProfile());
    }

    @FXML
    public void showSearchUsers() {
        loadView("/fxml/search_users.fxml", SearchUsersController.class);
    }

    @FXML
    public void showViewFriends() {
        loadView("/fxml/friends.fxml", FriendsController.class);
    }

    @FXML
    public void showViewConversations() {
        loadView("/fxml/conversations.fxml", ConversationsController.class);
    }

    @FXML
    public void showViewFriendRequests() {
        loadView("/fxml/friend_requests.fxml", FriendRequestsController.class);
    }

    private void loadUserProfile(UserProfile userProfile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/user_profile.fxml"));
            Node view = loader.load();
            UserProfileController controller = loader.getController();
            controller.setClient(client);
            controller.setUserProfile(userProfile);
            contentArea.getChildren().setAll(view);
        } catch (IOException e) {
            logger.error("Error loading user profile view", e);
            displayError("Error loading user profile view: " + e.getMessage());
        }
    }

    private <T> void loadView(String fxml, Class<T> controllerClass) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Node view = loader.load();
            T controller = loader.getController();
            if (controller instanceof SearchUsersController) {
                ((SearchUsersController) controller).setClient(client);
                ((SearchUsersController) controller).setHomeController(this);
            } else if (controller instanceof FriendsController) {
                ((FriendsController) controller).setClient(client);
                ((FriendsController) controller).setHomeController(this);
            } else if (controller instanceof ConversationsController) {
                ((ConversationsController) controller).setClient(client);
                ((ConversationsController) controller).setHomeController(this);
            } else if (controller instanceof FriendRequestsController) {
                ((FriendRequestsController) controller).setClient(client);
                ((FriendRequestsController) controller).setHomeController(this);
            }
            contentArea.getChildren().setAll(view);
        } catch (IOException e) {
            logger.error("Error loading view: " + fxml, e);
            displayError("Error loading view: " + e.getMessage());
        }
    }

    public void displayError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    public void handleLogout() {
        try {
            String response = client.logout();
            if ("success".equals(response)) {
                client.showLoginScreen();
            }
        } catch (IOException e) {
            logger.error("Error logging out", e);
            displayError("Error logging out: " + e.getMessage());
        }
    }
}