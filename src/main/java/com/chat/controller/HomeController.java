package com.chat.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chat.Client;
import com.chat.models.Conversation;
import com.chat.models.UserProfile;

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
    private Tab friendRequestsTab;
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
    private VBox friendRequestsBox;
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
        // Do nothing...
    }

    public void init() {
        new Thread(() -> {
            try {
                Platform.runLater(() -> displayCurrentUserProfile());
            } catch (Exception e) {
                logger.error("Error getting current user", e);
                Platform.runLater(() -> displayError("Error getting current user: " + e.getMessage()));
            }
        }).start();
    
        friendsTab.setOnSelectionChanged(event -> {
            if (friendsTab.isSelected()) {
                handleFriends();
            }
        });
    }

    public void displayCurrentUserProfile() {
        UserProfile profile = client.getSession().getUserProfile();
        profilePictureView.setImage(new Image(profile.getProfilePicture().toURI().toString()));
        usernameLabel.setText(profile.getUsername() + " (" + profile.getNickname() + ")");
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
                    Platform.runLater(() -> displayUserResults(results, searchResultsBox));
                } catch (IOException e) {
                    System.out.println("Error searching users: " + e.getMessage());
                }
            }).start();
        }
    }

    public void handleFriends() {
        new Thread(() -> {
            try {
                List<UserProfile> friends = client.getFriends();
                Platform.runLater(() -> displayUserResults(friends, friendsBox));
            } catch (IOException e) {
                System.out.println("Error getting friends: " + e.getMessage());
            }
        }).start();
    }

    public void handleFriendRequests() {
        new Thread(() -> {
            try {
                List<UserProfile> friendRequests = client.getFriendRequests();
                Platform.runLater(() -> displayFriendRequests(friendRequests));
            } catch (IOException e) {
                System.out.println("Error getting friend requests: " + e.getMessage());
            }
        }).start();
    }

    private void displayUserResults(List<UserProfile> results, VBox resultsBox) {
        resultsBox.getChildren().clear();
        if (!results.isEmpty()){
            for (UserProfile user : results) {
                HBox userBox = new HBox();
                userBox.setPadding(new Insets(0, 0, 0, 10));
                userBox.setAlignment(Pos.CENTER_LEFT);
                ImageView profilePictureView = new ImageView(new Image(user.getProfilePicture().toURI().toString()));
                profilePictureView.setFitHeight(50);
                profilePictureView.setFitWidth(50);
                Text usernameText = new Text(user.getUsername() + " (" + user.getNickname() + ")");
                usernameText.setFill(Color.BLUE);
                Text statusText = new Text(user.getStatus());
                statusText.setFill(Color.GREEN);
                userBox.getChildren().addAll(profilePictureView, usernameText, statusText);
                userBox.setOnMouseClicked(event -> displayUserProfile(user));
                resultsBox.getChildren().add(userBox);
            }
        } else {
            Text noResultsText = new Text("No results found");
            noResultsText.setFill(Color.RED);
            resultsBox.getChildren().add(noResultsText);
        }
    }

    private void displayUserProfile(UserProfile user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/user_profile.fxml"));
            Scene scene = new Scene(loader.load());
            UserProfileController controller = loader.getController();
            controller.setClient(client);
            controller.setUserProfile(user);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            logger.error("Error loading user profile screen", e);
        }
    }

    public void handleSendFriendRequest(UserProfile user) {
        new Thread(() -> {
            try {
                // Send a friend request to the user
                client.sendFriendRequest(user.getUserId());
                Platform.runLater(() -> displayError("Friend request sent successfully"));
            } catch (IOException e) {
                System.out.println("Error sending friend request: " + e.getMessage());
            }
        }).start();
    }

    public void handleRemoveFriend(UserProfile user) {
        new Thread(() -> {
            try {
                // Remove the user from the friends list
                client.removeFriend(user.getUserId());
                Platform.runLater(() -> displayError("Friend removed successfully"));
            } catch (IOException e) {
                System.out.println("Error removing friend: " + e.getMessage());
            }
        }).start();
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
                Text conversationText = new Text(conversation.getSenderUsername());
                conversationBox.getChildren().add(conversationText);
                messagesBox.getChildren().add(conversationBox);
            }
        }
    }

    private void displayFriendRequests(List<UserProfile> friendRequests) {
        friendRequestsBox.getChildren().clear();
        if (friendRequests.isEmpty()) {
            friendRequestsBox.getChildren().add(new Label("No friend requests"));
        } else {
            for (UserProfile userProfile : friendRequests) {
                HBox userBox = new HBox();
                userBox.setPadding(new Insets(0, 0, 0, 10));
                userBox.setAlignment(Pos.CENTER_LEFT);
                ImageView profilePictureView = new ImageView(new Image(userProfile.getProfilePicture().toURI().toString()));
                profilePictureView.setFitHeight(50);
                profilePictureView.setFitWidth(50);
                Text usernameText = new Text(userProfile.getUsername() + " (" + userProfile.getNickname() + ")");
                usernameText.setFill(Color.BLUE);
                Text statusText = new Text(userProfile.getStatus());
                statusText.setFill(Color.GREEN);
                Button acceptButton = new Button("Accept");
                acceptButton.setOnAction(event -> client.acceptFriendRequest(userProfile.getUserId()));
                Button denyButton = new Button("Deny");
                denyButton.setOnAction(event -> client.denyFriendRequest(userProfile.getUserId()));
                userBox.getChildren().addAll(profilePictureView, usernameText, statusText, acceptButton, denyButton);
                friendRequestsBox.getChildren().add(userBox);
            }
        }
    }

    public void handleStartNewConversation() {
        // Handle starting a new conversation
    }
}
