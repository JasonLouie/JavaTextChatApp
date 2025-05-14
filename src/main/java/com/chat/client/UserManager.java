package com.chat.client;

import java.io.File;
import java.io.IOException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chat.messages.*;
import com.chat.models.UserProfile;

public class UserManager {
    private static final Logger logger = LoggerFactory.getLogger(UserManager.class);
    private ConnectionManager connectionManager;
    private Client client;

    public UserManager(ConnectionManager connectionManager, Client client) {
        this.connectionManager = connectionManager;
        this.client = client;
    }

    public String handleLoginResponse(Message response) throws IOException {
        if (response instanceof LoginRegisterSuccessMessage) {
            LoginRegisterSuccessMessage successMessage = (LoginRegisterSuccessMessage) response;
            logger.info("Login successful, waiting for user and user profile...");
            Session session = new Session(successMessage.getUser(), successMessage.getProfile());
            client.setSession(session);
            logger.info("User and user profile received, session created");
            return "success";
        } else if (response instanceof ErrorMessage) {
            ErrorMessage errorMessage = (ErrorMessage) response;
            logger.error("Login failed: {}", errorMessage.getError());
            return errorMessage.getError();
        } else {
            logger.error("Invalid response type");
            throw new IOException("Invalid response type");
        }
    }

    public String handleRegisterResponse(Message response) throws IOException {
        if (response instanceof LoginRegisterSuccessMessage) {
            LoginRegisterSuccessMessage successMessage = (LoginRegisterSuccessMessage) response;
            logger.info("Registration successful, waiting for user and user profile...");
            Session session = new Session(successMessage.getUser(), successMessage.getProfile());
            client.setSession(session);
            return "success";
        } else if (response instanceof ErrorMessage) {
            ErrorMessage errorMessage = (ErrorMessage) response;
            logger.error("Registration failed: {}", errorMessage.getError());
            return errorMessage.getError();
        } else {
            logger.error("Invalid response type");
            throw new IOException("Invalid response type");
        }
    }

    public String login(String username, String password) {
        try {
            logger.info("Logging in...");
            LoginMessage message = new LoginMessage(username, password);
            connectionManager.sendMessage(message);
            Message response = connectionManager.readResponse();
            return handleLoginResponse(response);
        } catch (IOException e) {
            logger.error("Error logging in: {}", e.getMessage());
            return "Error logging in: " + e.getMessage();
        }
    }

    public String register(String username, String nickname, String email, String password, File profilePicture) throws IOException {
        logger.info("Registering...");
        try {
            RegisterMessage message = new RegisterMessage(username, nickname, email, password, profilePicture);
            connectionManager.sendMessage(message);
            Message response = connectionManager.readResponse();
            return handleRegisterResponse(response);
        } catch (IOException e) {
            logger.error("Error registering: {}", e.getMessage());
            return "Error registering: " + e.getMessage();
        }
    }

    public String logout() throws IOException {
        if (!client.isLoggedIn()) {
            logger.error("Not logged in");
            throw new IOException("Not logged in");
        }
        logger.info("Logging out user {}...", client.getUserId());
        RequestMessage message = new RequestMessage(RequestMessage.REQUEST_LOGOUT, client.getUserId());
        connectionManager.sendMessage(message);
        logger.info("Sent logout request...");
        Message response = connectionManager.readResponse();
        logger.info("Logout response received");
        if (response instanceof LogoutMessage) {
            LogoutMessage logoutMessage = (LogoutMessage) response;
            if (logoutMessage.isSuccess()) {
                logger.info("Logout successful");
                client.endSession();
                return "success";
            } else {
                logger.error("Logout failed");
                return "failure";
            }
        } else {
            throw new IOException("Invalid response type");
        }
    }

    public List<UserProfile> searchUsers(String query) {
        try {
            logger.info("Searching users for query: {}", query);
            RequestMessage message = new RequestMessage(RequestMessage.REQUEST_SEARCH_USERS, query);
            connectionManager.sendMessage(message);
            Message response = connectionManager.readResponse();
            logger.info("Received response from server: {}", response);
            if (response instanceof SearchUsersMessage) {
                SearchUsersMessage searchUsersMessage = (SearchUsersMessage) response;
                return searchUsersMessage.getProfiles();
            } else if (response instanceof NoResultsMessage) {
                NoResultsMessage noResultsMessage = (NoResultsMessage) response;
                logger.info(noResultsMessage.getMsg());
                return null;
            } else {
                logger.error("Invalid response type");
                return null;
            }
        } catch (Exception e) {
            logger.error("Error searching users: {}", e.getMessage());
            return null;
        }
    }
}
