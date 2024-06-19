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
import com.chat.messages.*;
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

    public synchronized void showFriendsScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/friends.fxml"));
            Scene scene = new Scene(loader.load());
            FriendsController controller = loader.getController();
            controller.setClient(this);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            logger.error("Error loading friends screen", e);
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
        try {
            logger.info("Logging in...");
            LoginMessage message = new LoginMessage(username, password);
            connectionManager.sendMessage(message);
            Message response = connectionManager.readResponse();
            return userManager.handleLoginResponse(response);
        } catch (IOException e) {
            logger.error("Error logging in: {}", e.getMessage());
            return "Error logging in: " + e.getMessage();
        }
    }

    public synchronized String logout() throws IOException {
        return userManager.logout();
    }

    public synchronized String register(String username, String nickname, String email, String password, File profilePicture) throws IOException {
        logger.info("Registering...");
        try {
            RegisterMessage message = new RegisterMessage(username, nickname, email, password, profilePicture);
            connectionManager.sendMessage(message);
            Message response = connectionManager.readResponse();
            return userManager.handleRegisterResponse(response);
        } catch (IOException e) {
            logger.error("Error registering: {}", e.getMessage());
            return "Error registering: " + e.getMessage();
        }
    }

    public synchronized List<UserProfile> searchUsers(String query) {
        return userManager.searchUsers(query);
    }

    public synchronized List<UserProfile> getFriends() {
        return friendManager.getFriends();
    }

    /*
    public synchronized List<UserProfile> searchUsers(String query) throws IOException {
        if (!isLoggedIn()) {
            logger.error("Not logged in");
            throw new IOException("Not logged in");
        }
        logger.info("Searching users of {}...", query);
        RequestMessage message = new RequestMessage(RequestMessage.REQUEST_SEARCH_USERS, query);
        message.writeTo(output);
        Message response = Message.readFrom(input);
        if (response instanceof SearchUsersMessage) {
            SearchUsersMessage searchUsersMessage = (SearchUsersMessage) response;
            logger.info("Received search results");
            return searchUsersMessage.getProfiles();
        } else if (response instanceof NoResultsMessage) {
            NoResultsMessage noResultsMessage = (NoResultsMessage) response;
            logger.info(noResultsMessage.getMsg());
            return null;
        }else {
            throw new IOException("Invalid response type");
        }
    }

    */

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
    /*
    public synchronized boolean sendFriendRequest(int userId) throws IOException {
        if (!isLoggedIn()) {
            throw new IOException("Not logged in");
        }
        logger.info("Sending friend request to user {}...", userId);
        RequestMessage message = new RequestMessage(RequestMessage.REQUEST_SEND_FRIEND_REQUEST, session.getUserProfile().getUserId(), userId);
        message.writeTo(output);
        Message response = Message.readFrom(input);
        if (response instanceof FriendSuccessMessage) {
            return ((FriendSuccessMessage) response).isSuccess();
        } else {
            throw new IOException("Invalid response type");
        }
    }
    
    public synchronized boolean cancelFriendRequest(int userId) throws IOException {
        if (!isLoggedIn()) {
            throw new IOException("Not logged in");
        }
        logger.info("Cancelling friend request...");
        RequestMessage message = new RequestMessage(RequestMessage.REQUEST_CANCEL_FRIEND_REQUEST, session.getUserProfile().getUserId(), userId);
        message.writeTo(output);
        Message response = Message.readFrom(input);
        if (response instanceof FriendSuccessMessage) {
            return ((FriendSuccessMessage) response).isSuccess();
        } else {
            throw new IOException("Invalid response type");
        }
    }
    
    public synchronized boolean removeFriend(int userId) throws IOException {
        if (!isLoggedIn()) {
            throw new IOException("Not logged in");
        }
        logger.info("Removing friend...");
        RequestMessage message = new RequestMessage(RequestMessage.REQUEST_REMOVE_FRIEND, session.getUserProfile().getUserId(), userId);
        message.writeTo(output);
        Message response = Message.readFrom(input);
        if (response instanceof FriendSuccessMessage) {
            return ((FriendSuccessMessage) response).isSuccess();
        } else {
            throw new IOException("Invalid response type");
        }
    }

    // Accept friend request
    public synchronized boolean acceptFriendRequest(int userId) throws IOException {
        if (!isLoggedIn()) {
            throw new IOException("Not logged in");
        }
        logger.info("Accepting friend request...");
        RequestMessage message = new RequestMessage(RequestMessage.REQUEST_ACCEPT_FRIEND_REQUEST, session.getUserProfile().getUserId(), userId);
        message.writeTo(output);
        Message response = Message.readFrom(input);
        if (response instanceof FriendSuccessMessage) {
            return ((FriendSuccessMessage) response).isSuccess();
        } else {
            throw new IOException("Invalid response type");
        }
    }

    // Deny friend request
    public synchronized boolean denyFriendRequest(int userId) throws IOException {
        if (!isLoggedIn()) {
            throw new IOException("Not logged in");
        }
        logger.info("Denying friend request...");
        RequestMessage message = new RequestMessage(RequestMessage.REQUEST_DENY_FRIEND_REQUEST, session.getUserProfile().getUserId(), userId);
        message.writeTo(output);
        Message response = Message.readFrom(input);
        if (response instanceof FriendSuccessMessage) {
            return ((FriendSuccessMessage) response).isSuccess();
        } else {
            throw new IOException("Invalid response type");
        }
    }

    // Get all friend requests
    public synchronized List<UserProfile> getFriendRequests() throws IOException {
        if (!isLoggedIn()) {
            throw new IOException("Not logged in");
        }
        logger.info("Getting friend requests...");
        RequestMessage message = new RequestMessage(RequestMessage.REQUEST_GET_FRIEND_REQUESTS, session.getUserProfile().getUserId());
        message.writeTo(output);
        Message response = Message.readFrom(input);
        if (response instanceof FriendRequestMessage) {
            FriendRequestMessage friendRequests = (FriendRequestMessage) response;
            logger.info("Received friend requests");
            return friendRequests.getFriendRequests();
        } else if (response instanceof NoResultsMessage) {
            NoResultsMessage noResultsMessage = (NoResultsMessage) response;
            logger.info(noResultsMessage.getMsg());
            return null;
        } else {
            throw new IOException("Invalid response type");
        }
    }
    */
}