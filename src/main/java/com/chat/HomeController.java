package com.chat;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

public class HomeController {
    private Client client;

    @FXML
    private TabPane tabPane;
    @FXML
    private Tab friendsTab;
    @FXML
    private Tab messagesTab;
    @FXML
    private Tab searchTab;
    @FXML
    private Button logoutButton;

    public void setClient(Client client) {
        this.client = client;
    }

    public void handleLogout() {
        client.showLoginScreen();
    }

    public void handleFriends() {
        client.showFriendsScreen();
    }

    public void handleMessages() {
        // Handle messages tab
    }

    public void handleSearch() {
        // Handle search tab
    }
}