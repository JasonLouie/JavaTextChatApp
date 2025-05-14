package com.chat.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import java.util.function.Consumer;

import com.chat.client.Client;

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

    /**
     * Sets the client instance for this controller.
     * 
     * @param client the client instance
     */
    public void setClient(Client client) {
        this.client = client;
    }

    /**
     * Handles a client request in a separate thread.
     * 
     * @param request  the request to handle
     * @param onSuccess callback to execute on success
     * @param onError   callback to execute on error
     */
    private void handleClientRequest(Runnable request, Runnable onSuccess, Consumer<Exception> onError) {
        new Thread(() -> {
            try {
                request.run();
                Platform.runLater(onSuccess);
            } catch (Exception e) {
                Platform.runLater(() -> onError.accept(e));
            }
        }).start();
    }

    /**
     * Handles the login button click event.
     */
    public void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please fill in all fields");
            return;
        }

        handleClientRequest(() -> client.login(username, password),
                () -> {
                    client.showHomeScreen();
                    errorLabel.setText("");
                },
                e -> errorLabel.setText("Error logging in: " + e.getMessage()));
    }

    /**
     * Handles the register button click event.
     */
    public void handleRegister() {
        client.showRegisterScreen();
    }
}