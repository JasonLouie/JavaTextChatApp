package com.chat;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Client extends Application {
    private Socket socket;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private Stage primaryStage;
    private BlockingQueue<String> messageQueue;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        messageQueue = new LinkedBlockingQueue<>();

        try {
            socket = new Socket("localhost", 8000);
            output = new ObjectOutputStream(socket.getOutputStream());
            output.flush(); // Flush the output stream after creation
            input = new ObjectInputStream(socket.getInputStream());
        } catch (UnknownHostException e) {
            System.out.println("Unknown host: " + e.getMessage());
            System.exit(1);
        } catch (IOException e) {
            System.out.println("Error connecting to server: " + e.getMessage());
            System.exit(1);
        }

        Thread listenerThread = new Thread(() -> {
            try {
                while (true) {
                    String message = input.readUTF();
                    messageQueue.put(message); // Put the message into the queue
                    Platform.runLater(() -> {
                        // Optionally handle incoming message from server for UI updates
                        System.out.println("Received message from server: " + message);
                    });
                }
            } catch (IOException | InterruptedException e) {
                System.out.println("Error reading message from server: " + e.getMessage());
            }
        });
        listenerThread.start();

        showLoginScreen();
    }

    public void showLoginScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Scene scene = new Scene(loader.load());
            LoginController controller = loader.getController();
            controller.setClient(this);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            System.out.println("Error loading login screen: " + e.getMessage());
        }
    }

    public void showRegisterScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/register.fxml"));
            Scene scene = new Scene(loader.load());
            RegisterController controller = loader.getController();
            controller.setClient(this);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            System.out.println("Error loading register screen: " + e.getMessage());
        }
    }

    public void showHomeScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/home.fxml"));
            Scene scene = new Scene(loader.load());
            HomeController controller = loader.getController();
            controller.setClient(this);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            System.out.println("Error loading home screen: " + e.getMessage());
        }
    }

    public void showFriendsScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/friends.fxml"));
            Scene scene = new Scene(loader.load());
            FriendsController controller = loader.getController();
            controller.setClient(this);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            System.out.println("Error loading friends screen: " + e.getMessage());
        }
    }

    public synchronized String login(String username, String password) throws IOException {
        System.out.println("Logging in...");
        output.writeUTF("login");
        output.flush(); // Ensure the data is sent immediately
        output.writeUTF(username);
        output.flush(); // Ensure the data is sent immediately
        output.writeUTF(password);
        output.flush(); // Ensure the data is sent immediately
        System.out.println("Sent credentials...");
        try {
            return messageQueue.take(); // Wait and take the response from the queue
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Interrupted while waiting for server response", e);
        }
    }

    public synchronized String register(String username, String nickname, String email, String password) throws IOException {
        System.out.println("Registering...");
        output.writeUTF("register");
        output.flush(); // Ensure the data is sent immediately
        output.writeUTF(username);
        output.flush(); // Ensure the data is sent immediately
        output.writeUTF(nickname);
        output.flush(); // Ensure the data is sent immediately
        output.writeUTF(email);
        output.flush(); // Ensure the data is sent immediately
        output.writeUTF(password);
        output.flush(); // Ensure the data is sent immediately
        try {
            return messageQueue.take(); // Wait and take the response from the queue
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Interrupted while waiting for server response", e);
        }
    }

    public synchronized String getFriends() throws IOException {
        output.writeUTF("get_friends");
        output.flush(); // Ensure the data is sent immediately
        try {
            return messageQueue.take(); // Wait and take the response from the queue
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Interrupted while waiting for server response", e);
        }
    }
}
