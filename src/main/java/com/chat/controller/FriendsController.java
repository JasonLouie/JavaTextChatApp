package com.chat.controller;

import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import com.chat.client.Client;
import com.chat.models.UserProfile;
import java.util.List;
import javafx.application.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FriendsController {
    @FXML
    private VBox friendsBox;

    private Client client;
    private HomeController homeController;

    private static final Logger logger = LoggerFactory.getLogger(FriendsController.class);

    public void setClient(Client client) {
        this.client = client;
        loadFriends();
    }

    public void setHomeController(HomeController homeController) {
        this.homeController = homeController;
    }

    private void loadFriends() {
        new Thread(() -> {
            try {
                List<UserProfile> friends = client.getFriends();
                Platform.runLater(() -> displayFriends(friends));
            } catch (Exception e) {
                logger.error("Error loading friends", e);
                Platform.runLater(() -> {
                    if (homeController != null) {
                        homeController.displayError("Error loading friends: " + e.getMessage());
                    }
                });
            }
        }).start();
    }

    private void displayFriends(List<UserProfile> friends) {
        friendsBox.getChildren().clear();
        if (friends != null && !friends.isEmpty()) {
            for (UserProfile friend : friends) {
                Label friendLabel = new Label(friend.getUsername() + " (" + friend.getNickname() + ")");
                friendsBox.getChildren().add(friendLabel);
            }
        } else {
            Label noFriendsLabel = new Label("No friends found.");
            friendsBox.getChildren().add(noFriendsLabel);
        }
    }
}
