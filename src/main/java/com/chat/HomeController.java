package com.chat;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HomeController {
    private Client client;
    private Logger logger = LoggerFactory.getLogger(HomeController.class);
    @FXML
    private TabPane tabPane;
    @FXML
    private Tab homeTab;
    @FXML
    private Tab friendsTab;
    @FXML
    private Tab messagesTab;
    @FXML
    private Tab searchTab;
    @FXML
    private Button logoutButton;
    @FXML
    private ImageView profilePictureView;
    @FXML
    private Label usernameLabel;
    @FXML
    private Label bioLabel;
    @FXML
    private Label statusLabel;
    @FXML
    private Label friendsCountLabel;
    @FXML
    private TextField searchField;
    @FXML
    private VBox searchResultsBox;
    @FXML
    private VBox friendsBox;
    @FXML
    private VBox messagesBox;
    @FXML
    private Button startConversationButton;

    public void setClient(Client client) {
        this.client = client;
    }

    public void initialize() {
        new Thread(() -> {
            try {
                Platform.runLater(() -> displayCurrentUserProfile());
            } catch (Exception e) {
                logger.error("Error getting current user", e);
                Platform.runLater(() -> displayError("Error getting current user: " + e.getMessage()));
            }
        }).start();
    }

    public void displayCurrentUserProfile() {
        UserProfile profile = client.getSession().getUserProfile();
        profilePictureView.setImage(new Image(profile.getProfilePicture().toURI().toString()));
        usernameLabel.setText(profile.getUsername());
        bioLabel.setText(profile.getBio());
        statusLabel.setText(profile.getStatus());
        // friendsCountLabel.setText(String.valueOf(profile.getFriendsCount()));
    }

    public void displayError(String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void handleLogout() {
        new Thread(() -> {
            try {
                String response = client.logout();
                if (response.equals("success")) {
                    Platform.runLater(() -> client.showLoginScreen());
                }
            } catch (IOException e) {
                System.out.println("Error logging out: " + e.getMessage());
            }
        }).start();
    }

    public void handleSearch() {
        String query = searchField.getText();
        if (!query.isEmpty()) {
            new Thread(() -> {
                try {
                    List<UserProfile> results = client.searchUsers(query);
                    Platform.runLater(() -> displaySearchResults(results));
                } catch (IOException e) {
                    System.out.println("Error searching users: " + e.getMessage());
                }
            }).start();
        }
    }

    private void displaySearchResults(List<UserProfile> results) {
        searchResultsBox.getChildren().clear();
        for (UserProfile user : results) {
            VBox userBox = new VBox();
            ImageView profilePictureView = new ImageView(new Image(user.getProfilePicture().toURI().toString()));
            profilePictureView.setFitHeight(50);
            profilePictureView.setFitWidth(50);
            Text usernameText = new Text(user.getUsername());
            usernameText.setFill(Color.BLUE);
            Text nicknameText = new Text(user.getNickname());
            nicknameText.setFill(Color.GREEN);
            userBox.getChildren().addAll(profilePictureView, usernameText, nicknameText);
            searchResultsBox.getChildren().add(userBox);
        }
    }

    public void handleFriends() {
        new Thread(() -> {
            try {
                List<UserProfile> friends = client.getFriends();
                Platform.runLater(() -> displayFriends(friends));
            } catch (IOException e) {
                System.out.println("Error getting friends: " + e.getMessage());
            }
        }).start();
    }

    private void displayFriends(List<UserProfile> friends) {
        friendsBox.getChildren().clear();
        for (UserProfile user : friends) {
            VBox userBox = new VBox();
            ImageView profilePictureView = new ImageView(new Image(user.getProfilePicture().toURI().toString()));
            profilePictureView.setFitHeight(50);
            profilePictureView.setFitWidth(50);
            Text usernameText = new Text(user.getUsername());
            usernameText.setFill(Color.BLUE);
            Text nicknameText = new Text(user.getNickname());
            nicknameText.setFill(Color.GREEN);
            userBox.getChildren().addAll(profilePictureView, usernameText, nicknameText);
            friendsBox.getChildren().add(userBox);
        }
    }

    public void handleMessages() {
        new Thread(() -> {
            try {
                List<Conversation> conversations = client.getConversations();
                Platform.runLater(() -> displayConversations(conversations));
            } catch (IOException e) {
                System.out.println("Error getting conversations: " + e.getMessage());
            }
        }).start();
    }

    private void displayConversations(List<Conversation> conversations) {
        messagesBox.getChildren().clear();
        if (conversations.isEmpty()) {
            messagesBox.getChildren().add(new Label("No conversations"));
        } else {
            for (Conversation conversation : conversations) {
                VBox conversationBox = new VBox();
                Text conversationText = new Text(conversation.getUsername());
                conversationBox.getChildren().add(conversationText);
                messagesBox.getChildren().add(conversationBox);
            }
        }
    }

    public void handleStartConversation() {
        // Handle starting a new conversation
    }
}