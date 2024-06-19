package com.chat.controller;

import com.chat.client.Client;
import com.chat.models.UserProfile;

import java.io.*;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class UserProfileController {
    private Client client;
    private UserProfile userProfile;

    @FXML
    private ImageView profilePictureView;
    @FXML
    private Label usernameLabel;
    @FXML
    private Label bioLabel;
    @FXML
    private Label statusLabel;
    @FXML
    private Button backButton;
    @FXML
    private Button friendButton;

    public void setClient(Client client) {
        this.client = client;
    }

    public void setUserProfile(UserProfile userProfile) {
        this.userProfile = userProfile;
        displayUserProfile();
    }

    private void displayUserProfile() {
        profilePictureView.setImage(new Image(userProfile.getProfilePicture().toURI().toString()));
        usernameLabel.setText(userProfile.getUsername() + " (" + userProfile.getNickname() + ")");
        bioLabel.setText(userProfile.getBio());
        statusLabel.setText(userProfile.getStatus());
        backButton.setOnAction(event -> handleBackButton());
        friendButton.setOnAction(event -> handleFriendButton());
        // Check if the user is already friends with the client
        new Thread(() -> {
            try {
                boolean isFriends = client.friendsWith(userProfile.getUserId());
                if (isFriends) {
                    Platform.runLater(() -> friendButton.setText("Remove Friend"));
                } else {
                    // Check if there's a pending friend request
                    boolean hasPendingRequest = client.hasFriendRequest(userProfile.getUserId());
                    if (hasPendingRequest) {
                        Platform.runLater(() -> friendButton.setText("Cancel Friend Request"));
                    } else {
                        Platform.runLater(() -> friendButton.setText("Add Friend"));
                    }
                }
            } catch (IOException e) {
                System.out.println("Error checking friendship status: " + e.getMessage());
            }
        }).start();
    }

    private void handleBackButton() {
        client.showHomeScreen();
    }

    private void handleFriendButton() {
        // Handle the friend button action based on the current text
        if (friendButton.getText().equals("Add Friend")) {
            handleSendFriendRequest();
        } else if (friendButton.getText().equals("Remove Friend")) {
            handleRemoveFriend();
        } else if (friendButton.getText().equals("Cancel Friend Request")) {
            handleCancelFriendRequest();
        }
    }

    private void handleSendFriendRequest() {
        new Thread(() -> {
            try {
                client.sendFriendRequest(userProfile.getUserId());
                Platform.runLater(() -> friendButton.setText("Cancel Friend Request"));
            } catch (Exception e) {
                System.out.println("Error sending friend request: " + e.getMessage());
            }
        }).start();
    }

    private void handleRemoveFriend() {
        new Thread(() -> {
            try {
                client.removeFriend(userProfile.getUserId());
                Platform.runLater(() -> friendButton.setText("Add Friend"));
            } catch (Exception e) {
                System.out.println("Error removing friend: " + e.getMessage());
            }
        }).start();
    }

    private void handleCancelFriendRequest() {
        new Thread(() -> {
            try {
                client.cancelFriendRequest(userProfile.getUserId());
                Platform.runLater(() -> friendButton.setText("Add Friend"));
            } catch (Exception e) {
                System.out.println("Error canceling friend request: " + e.getMessage());
            }
        }).start();
    }
}
