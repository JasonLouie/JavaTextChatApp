package com.chat;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import java.io.*;

public class RegisterController {
    private Client client;

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
    private Label errorLabel;

    public void setClient(Client client) {
        this.client = client;
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

        new Thread(() -> {
            try {
                String response = client.register(username, nickname, email, password);
                if (response.equals("success")) {
                    Platform.runLater(() -> client.showHomeScreen());
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