package com.chat.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import java.io.*;

import com.chat.Client;

public class LoginController {
    private Client client;

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button loginButton;
    @FXML
    private Button registerButton;
    @FXML
    private Label errorLabel;

    public void setClient(Client client) {
        this.client = client;
    }

    public void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please fill in all fields");
            return;
        }

        new Thread(() -> {
            try {
                String response = client.login(username, password);
                if (response.equals("success")) {
                    Platform.runLater(() -> client.showHomeScreen());
                    Platform.runLater(() ->errorLabel.setText(""));
                }else {
                    Platform.runLater(() -> errorLabel.setText("Login failed: " + response));
                }
            } catch (IOException e) {
                Platform.runLater(() -> errorLabel.setText("Error logging in: " + e.getMessage()));
            }
        }).start();
    }

    public void handleRegister() {
        client.showRegisterScreen();
    }
}