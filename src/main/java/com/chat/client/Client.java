package com.chat.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.*;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chat.controller.*;
import com.chat.models.*;

public class Client extends Application {
    private static final Logger logger = LoggerFactory.getLogger(Client.class);
    private ConnectionManager connectionManager;
    private UserManager userManager;
    private ConversationManager conversationManager;
    private FriendManager friendManager;
    private FriendRequestsManager friendRequestsManager;
    private Stage primaryStage;
    private Session session;

    public static void main(String[] args) {
        launch(args);
    }

    public Client() {
        connectionManager = new ConnectionManager();
        userManager = new UserManager(connectionManager, this);
        conversationManager = new ConversationManager(connectionManager, this);
        friendManager = new FriendManager(connectionManager, this);
        friendRequestsManager = new FriendRequestsManager(connectionManager, this);
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        connectionManager.connect();
        showLoginScreen();
    }

    public synchronized void close() {
        logger.info("Closing client...");
        if (session != null) {
            try {
                userManager.logout();
            } catch (IOException e) {
                logger.error("Error during logout", e);
            }
        }
        connectionManager.close();
    }

    public ConnectionManager getConnectionManager() {
        return connectionManager;
    }

    public UserManager getUserManager() {
        return userManager;
    }

    public ConversationManager getConversationManager() {
        return conversationManager;
    }

    public FriendManager getFriendManager() {
        return friendManager;
    }

    public FriendRequestsManager getFriendRequestsManager() {
        return friendRequestsManager;
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public UserProfile getUserProfile() {
        return session.getUserProfile();
    }

    public int getUserId() {
        return session.getUserProfile().getUserId();
    }

    public boolean isLoggedIn() {
        return session != null;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public Session getSession() {
        return session;
    }

    public void endSession() {
        session = null;
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
            logger.error("Error loading login screen", e);
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
            logger.error("Error loading register screen", e);
        }
    }
   
    public void showHomeScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/home.fxml"));
            Scene scene = new Scene(loader.load());
            HomeController controller = loader.getController();
            controller.setClient(this);
            controller.init(); // Call the init method here
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            logger.error("Error loading home screen", e);
        }
    }

    public synchronized void showProfileScreen(UserProfile user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/user_profile.fxml"));
            Scene scene = new Scene(loader.load());
            UserProfileController controller = loader.getController();
            controller.setClient(this);
            controller.setUserProfile(user);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            logger.error("Error loading user profile screen", e);
        }
    }

    public synchronized String login(String username, String password) {
        return userManager.login(username, password);
    }

    public synchronized String logout() throws IOException {
        return userManager.logout();
    }

    public synchronized String register(String username, String nickname, String email, String password, File profilePicture) throws IOException {
        return userManager.register(username, nickname, email, password, profilePicture);
    }

    public synchronized List<UserProfile> searchUsers(String query) {
        return userManager.searchUsers(query);
    }

    public synchronized List<UserProfile> getFriends() {
        return friendManager.getFriends();
    }

    public synchronized boolean friendsWith(int userId) throws IOException {
        return friendManager.friendsWith(userId);
    }

    public synchronized boolean removeFriend(int userId) {
        return friendManager.removeFriend(userId);
    }

    public synchronized List<UserProfile> getFriendRequests() {
        return friendRequestsManager.getFriendRequests();
    }

    public synchronized boolean sendFriendRequest(int userId) {
        return friendRequestsManager.sendFriendRequest(userId);
    }

    public synchronized boolean cancelFriendRequest(int userId) {
        return friendRequestsManager.cancelFriendRequest(userId);
    }

    public synchronized boolean acceptFriendRequest(int userId) {
        return friendRequestsManager.acceptFriendRequest(userId);
    }

    public synchronized boolean denyFriendRequest(int userId) {
        return friendRequestsManager.denyFriendRequest(userId);
    }

    public synchronized boolean hasFriendRequest(int userId) {
        return friendRequestsManager.hasFriendRequest(userId);
    }

    public synchronized List<Conversation> getConversations() {
        return conversationManager.getConversations();
    }

    public synchronized List<Content> getConversation(int userId) {
        return conversationManager.getConversation(userId);
    }
}