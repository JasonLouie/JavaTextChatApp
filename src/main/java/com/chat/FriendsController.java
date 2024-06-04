package com.chat;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import java.io.*;

public class FriendsController {
    private Client client;

    @FXML
    private ListView<String> friendsListView;
    @FXML
    private Label noFriendsLabel;

    public void setClient(Client client) {
        this.client = client;
    }

    public void initialize() {
        try {
            String response = client.getFriends();
            if (response.isEmpty()) {
                noFriendsLabel.setVisible(true);
            } else {
                friendsListView.getItems().addAll(response.split(","));
            }
        } catch (IOException e) {
            noFriendsLabel.setText("Error getting friends: " + e.getMessage());
        }
    }
}