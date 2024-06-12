package com.chat;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;
import java.util.UUID;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chat.controller.*;
import com.chat.messages.*;
import com.chat.models.*;

public class Client extends Application {
    private static final Logger logger = LoggerFactory.getLogger(Client.class);
    private Socket socket;
    private DataOutputStream output;
    private DataInputStream input;
    private Stage primaryStage;
    private Session session;

    public static void main(String[] args) {
        launch(args);
    }

    public Client(Socket socket) {
        this.socket = socket;
    }

    public Client() {
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        if (socket == null) {
            try {
                socket = new Socket("localhost", 8000);
            } catch (Exception e) {
                logger.error("Error creating socket", e);
                System.exit(1);
            }
        }
        connect();
        showLoginScreen();
    }

    public void connect() {
        try {
            if (socket == null) {
                throw new IOException("Socket is not initialized");
            }
            logger.info("Connecting to server...");
            output = new DataOutputStream(socket.getOutputStream());
            input = new DataInputStream(socket.getInputStream());
            logger.info("Connected to server");
        } catch (UnknownHostException e) {
            logger.error("Unknown host", e);
            System.exit(1);
        } catch (IOException e) {
            logger.error("Error connecting to server", e);
            System.exit(1);
        }
    }

    public void close() {
        logger.info("Closing client...");
        if (session != null) {
            try {
                logout();
            } catch (IOException e) {
                logger.error("Error during logout", e);
            }
        }
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
            if (input != null) {
                input.close();
            }
            if (output != null) {
                output.close();
            }
        } catch (IOException e) {
            logger.error("Error closing client connection", e);
        } finally {
            socket = null;
            input = null;
            output = null;
        }
    }

    public Session getSession() {
        return session;
    }

    private String generateSessionToken() {
        return UUID.randomUUID().toString();
    }

    public boolean isLoggedIn() {
        return session != null;
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

    public synchronized String login(String username, String password) throws IOException {
        logger.info("Logging in...");
        LoginMessage message = new LoginMessage(username, password);
        message.writeTo(output);
        logger.info("Sent credentials...");
        Message response = Message.readFrom(input);
        logger.info("Login response: " + response);
        if (response instanceof LoginRegisterSuccessMessage) {
            LoginRegisterSuccessMessage successMessage = (LoginRegisterSuccessMessage) response;
            logger.info("Login successful, waiting for user profile...");
            session = new Session(new User(username), successMessage.getProfile(), generateSessionToken());
            logger.info("User profile received, session created");
            return "success";
        } else if (response instanceof ErrorMessage) {
            ErrorMessage errorMessage = (ErrorMessage) response;
            return errorMessage.getError();
        } else {
            throw new IOException("Invalid response type");
        }
    }

    public synchronized String logout() throws IOException {
        logger.info("Logging out user {}...", session.getUserProfile().getUserId());
        if (session == null) {
            throw new IOException("Not logged in");
        }
        RequestMessage message = new RequestMessage(RequestMessage.REQUEST_LOGOUT, session.getUserProfile().getUserId());
        message.writeTo(output);
        logger.info("Sent logout request...");
        Message response = Message.readFrom(input);
        logger.info("Logout response received");
        if (response instanceof LogoutMessage) {
            LogoutMessage logoutMessage = (LogoutMessage) response;
            if (logoutMessage.isSuccess()) {
                logger.info("Logout successful");
                session = null;
                return "success";
            } else {
                logger.error("Logout failed");
                return "failed";
            }
        } else {
            throw new IOException("Invalid response type");
        }
    }

    public synchronized String register(String username, String nickname, String email, String password, File profilePicture) throws IOException {
        logger.info("Registering...");
        RegisterMessage message = new RegisterMessage(username, nickname, email, password, profilePicture);
        message.writeTo(output);
        output.flush();
        logger.info("Sent registration info...");
        Message response = Message.readFrom(input);
        if (response instanceof LoginRegisterSuccessMessage) {
            LoginRegisterSuccessMessage successMessage = (LoginRegisterSuccessMessage) response;
            logger.info("Registration successful, waiting for user profile...");
            session = new Session(new User(username), successMessage.getProfile(), generateSessionToken());
            logger.info("User profile received, session created");
            return "success";
        } else if (response instanceof ErrorMessage) {
            ErrorMessage errorMessage = (ErrorMessage) response;
            return errorMessage.getError();
        } else {
            throw new IOException("Invalid response type");
        }
    }

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
            return new ArrayList<>();
        }else {
            throw new IOException("Invalid response type");
        }
    }

    public synchronized List<UserProfile> getFriends() throws IOException {
        if (!isLoggedIn()) {
            throw new IOException("Not logged in");
        }
        logger.info("Getting friends list...");
        RequestMessage message = new RequestMessage(RequestMessage.REQUEST_GET_FRIENDS, session.getUserProfile().getUserId());
        message.writeTo(output);
        Message response = Message.readFrom(input);
        if (response instanceof FriendsListMessage) {
            FriendsListMessage friendsListMessage = (FriendsListMessage) response;
            logger.info("Received friends list");
            return friendsListMessage.getFriends();
        } else if (response instanceof NoResultsMessage) {
            NoResultsMessage noResultsMessage = (NoResultsMessage) response;
            logger.info(noResultsMessage.getMsg());
            return new ArrayList<>();
        }else {
            throw new IOException("Invalid response type");
        }
    }

    public synchronized List<Conversation> getConversations() throws IOException {
        if (!isLoggedIn()) {
            throw new IOException("Not logged in");
        }
        logger.info("Getting conversations list...");
        Message message = Message.readFrom(input);
        if (message instanceof ConversationsMessage) {
            ConversationsMessage conversationsMessage = (ConversationsMessage) message;
            logger.info("Received conversations list");
            return conversationsMessage.getConversations();
        } else {
            throw new IOException("Invalid response type");
        }
    }

    public synchronized List<Content> getConversation(int userId) throws IOException {
        if (!isLoggedIn()) {
            throw new IOException("Not logged in");
        }
        RequestMessage request = new RequestMessage(RequestMessage.REQUEST_GET_CONVERSATION, session.getUserProfile().getUserId(), userId);
        request.writeTo(output);
        Message response = Message.readFrom(input);
        if (response instanceof ConversationMessage) {
            ConversationMessage conversationMessage = (ConversationMessage) response;
            return conversationMessage.getConversation();
        } else {
            throw new IOException("Invalid response type");
        }
    }

    public synchronized boolean friendsWith(int userId) throws IOException {
        if (!isLoggedIn()) {
            throw new IOException("Not logged in");
        }
        RequestMessage message = new RequestMessage(RequestMessage.REQUEST_FRIENDS_WITH, session.getUserProfile().getUserId(), userId);
        message.writeTo(output);
        Message response = Message.readFrom(input);
        if (response instanceof FriendSuccessMessage) {
            return ((FriendSuccessMessage) response).isSuccess();
        } else {
            throw new IOException("Invalid response type");
        }
    }
    
    public synchronized boolean hasFriendRequest(int userId) throws IOException {
        if (!isLoggedIn()) {
            throw new IOException("Not logged in");
        }
        RequestMessage message = new RequestMessage(RequestMessage.REQUEST_HAS_FRIEND_REQUEST, session.getUserProfile().getUserId(), userId);
        message.writeTo(output);
        Message response = Message.readFrom(input);
        if (response instanceof FriendSuccessMessage) {
            return ((FriendSuccessMessage) response).isSuccess();
        } else {
            throw new IOException("Invalid response type");
        }
    }
    
    public synchronized boolean sendFriendRequest(int userId) throws IOException {
        if (!isLoggedIn()) {
            throw new IOException("Not logged in");
        }
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
            return new ArrayList<>();
        } else {
            throw new IOException("Invalid response type");
        }
    }
}