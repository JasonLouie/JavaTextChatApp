package com.chat.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chat.client.Client;
import com.chat.models.UserProfile;

public class FriendsController {
    private Client client;
    private static final Logger logger = LoggerFactory.getLogger(FriendsController.class);
    @FXML
    private ListView<String> friendsListView;
    @FXML
    private Label noFriendsLabel;

    public void setClient(Client client) {
        this.client = client;
    }

    public void initialize() {
        new Thread(() -> {
            try {
                List<UserProfile> friends = client.getFriends();
                Platform.runLater(() -> displayFriends(friends));
            } catch (Exception e) {
                logger.error("Error getting friends", e);
                Platform.runLater(() -> displayError("Error getting friends: " + e.getMessage()));
            }
        }).start();
    }
    
    private void displayFriends(List<UserProfile> friends) {
        friendsListView.getItems().clear();
        if (friends.isEmpty()) {
            noFriendsLabel.setVisible(true);
        } else {
            for (UserProfile friend : friends) {
                friendsListView.getItems().add(friend.getUsername());
            }
        }
    }
    
    private void displayError(String message) {
        noFriendsLabel.setText(message);
        noFriendsLabel.setVisible(true);
    }
}