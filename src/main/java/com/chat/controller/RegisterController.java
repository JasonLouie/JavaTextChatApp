package com.chat.controller;

import javafx.fxml.FXML;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import java.io.*;

import com.chat.Client;

public class RegisterController {
    private Client client;
    private File profilePicture;

    @FXML
    private TextField usernameField;
    @FXML
    private TextField nicknameField;
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private Button registerButton;
    @FXML
    private Button backButton;
    @FXML
    private Button chooseImageButton;
    @FXML
    private ImageView profilePictureView;
    @FXML
    private Label errorLabel;

    public void setClient(Client client) {
        this.client = client;
    }

    public void handleChooseImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Profile Picture");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );
        profilePicture = fileChooser.showOpenDialog(null);
        if (profilePicture != null) {
            Image image = new Image(profilePicture.toURI().toString());
            profilePictureView.setImage(image);
        }
    }

    public void handleRegister() {
        String username = usernameField.getText();
        String nickname = nicknameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (username.isEmpty() || nickname.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            errorLabel.setText("Please fill in all fields");
            return;
        }

        if (!password.equals(confirmPassword)) {
            errorLabel.setText("Passwords do not match");
            return;
        }

        if (profilePicture == null) {
            errorLabel.setText("Please choose a profile picture");
            return;
        }

        new Thread(() -> {
            try {
                String response = client.register(username, nickname, email, password, profilePicture);
                if (response.equals("success")) {
                    Platform.runLater(() -> client.showHomeScreen());
                    Platform.runLater(() -> errorLabel.setText(""));
                } else {
                    Platform.runLater(() -> errorLabel.setText("Registration failed: " + response));
                }
            } catch (IOException e) {
                Platform.runLater(() -> errorLabel.setText("Error registering: " + e.getMessage()));
            }
        }).start();
    }

    public void handleBack() {
        client.showLoginScreen();
    }
}