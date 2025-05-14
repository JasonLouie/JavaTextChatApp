package com.chat.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chat.client.Client;
import com.chat.models.UserProfile;

public class SearchUsersController {
    @FXML
    private TextField searchField;
    @FXML
    private VBox searchResultsBox;

    private Client client;
    private HomeController homeController;

    private static final Logger logger = LoggerFactory.getLogger(SearchUsersController.class);


    public void setClient(Client client) {
        this.client = client;
    }

    public void setHomeController(HomeController homeController) {
        this.homeController = homeController;
    }

    @FXML
    private void handleSearch() {
        String query = searchField.getText();
        if (!query.isEmpty()) {
            new Thread(() -> {
                try {
                    List<UserProfile> results = client.searchUsers(query);
                    Platform.runLater(() -> displayUserResults(results));
                } catch (Exception e) {
                    logger.error("Error searching users", e);
                    Platform.runLater(() -> {
                        if (homeController != null) {
                            homeController.displayError("Error searching users: " + e.getMessage());
                        } else {
                            displayError("Error searching users: " + e.getMessage());
                        }
                    });
                }
            }).start();
        }
    }

    private void displayUserResults(List<UserProfile> results) {
        searchResultsBox.getChildren().clear();
        if (results != null) {
            for (UserProfile user : results) {
                HBox userBox = createUserBox(user);
<<<<<<< HEAD
                userBox.setOnMouseClicked(_ -> client.showProfileScreen(user));
=======
                userBox.setOnMouseClicked(event -> client.showProfileScreen(user));
>>>>>>> 23ed6ab2476e60aa1fb0bd279282a30056e505e3
                searchResultsBox.getChildren().add(userBox);
            }
        } else {
            Text noResultsText = new Text("No results found");
            noResultsText.getStyleClass().add("no-results-text");
            searchResultsBox.getChildren().add(noResultsText);
        }
    }

    private HBox createUserBox(UserProfile user) {
        HBox userBox = new HBox();
        userBox.getStyleClass().add("user-box");
        userBox.setPadding(new Insets(10));
        userBox.setAlignment(Pos.CENTER_LEFT);
        ImageView profilePictureView = new ImageView(new Image(user.getProfilePicture().toURI().toString()));
        profilePictureView.setFitHeight(50);
        profilePictureView.setFitWidth(50);
        Text usernameText = new Text(user.getUsername() + " (" + user.getNickname() + ")");
        usernameText.getStyleClass().add("username-text");
        Text statusText = new Text(user.getStatus());
        statusText.getStyleClass().add("status-text");
        userBox.getChildren().addAll(profilePictureView, usernameText, statusText);
        return userBox;
    }

    private void displayError(String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}