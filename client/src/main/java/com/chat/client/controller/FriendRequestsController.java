package com.chat.controller;

import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.control.Button;
import com.chat.client.Client;
import com.chat.models.UserProfile;
import java.util.List;
import javafx.application.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FriendRequestsController {
    @FXML
    private VBox friendRequestsBox;

    private Client client;
    private HomeController homeController;

    private static final Logger logger = LoggerFactory.getLogger(FriendRequestsController.class);

    public void setClient(Client client) {
        this.client = client;
        loadFriendRequests();
    }

    public void setHomeController(HomeController homeController) {
        this.homeController = homeController;
    }

    public VBox getFriendRequestsBox() {
        return friendRequestsBox;
    }

    public void loadFriendRequests() {
        new Thread(() -> {
            try {
                List<UserProfile> friendRequests = client.getFriendRequests();
                Platform.runLater(() -> displayFriendRequests(friendRequests));
            } catch (Exception e) {
                logger.error("Error loading friend requests", e);
                Platform.runLater(() -> {
                    if (homeController != null) {
                        homeController.displayError("Error loading friend requests: " + e.getMessage());
                    }
                });
            }
        }).start();
    }

    private void displayFriendRequests(List<UserProfile> friendRequests) {
        friendRequestsBox.getChildren().clear();
        if (friendRequests != null && !friendRequests.isEmpty()) {
            for (UserProfile request : friendRequests) {
                HBox requestBox = new HBox();
                requestBox.getStyleClass().add("friend-request-box");

                ImageView profilePictureView = new ImageView(new Image(request.getProfilePicture().toURI().toString()));
                profilePictureView.setFitHeight(40);
                profilePictureView.setFitWidth(40);
                profilePictureView.getStyleClass().add("profile-picture");

                Label requestLabel = new Label(request.getUsername() + " (" + request.getNickname() + ")");
                requestLabel.getStyleClass().add("request-label");
                HBox.setHgrow(requestLabel, Priority.ALWAYS);

                Button acceptButton = new Button("Accept");
                acceptButton.getStyleClass().add("accept-button");
                acceptButton.setOnAction(event -> {
                    client.acceptFriendRequest(request.getUserId());
                    loadFriendRequests();
                });

                Button denyButton = new Button("Deny");
                denyButton.getStyleClass().add("deny-button");
                denyButton.setOnAction(event -> {
                    client.denyFriendRequest(request.getUserId());
                    loadFriendRequests();
                });

                requestBox.getChildren().addAll(profilePictureView, requestLabel, acceptButton, denyButton);
                friendRequestsBox.getChildren().add(requestBox);
            }
        } else {
            Label noRequestsLabel = new Label("No friend requests found.");
            noRequestsLabel.getStyleClass().add("no-requests-label");
            friendRequestsBox.getChildren().add(noRequestsLabel);
        }
    }
}