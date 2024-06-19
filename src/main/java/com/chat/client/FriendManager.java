package com.chat.client;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chat.messages.FriendSuccessMessage;
import com.chat.messages.FriendsListMessage;
import com.chat.messages.Message;
import com.chat.messages.NoResultsMessage;
import com.chat.messages.RequestMessage;
import com.chat.models.UserProfile;

public class FriendManager {
    private static final Logger logger = LoggerFactory.getLogger(FriendManager.class);
    private ConnectionManager connectionManager;
    private Client client;

    public FriendManager(ConnectionManager connectionManager, Client client) {
        this.connectionManager = connectionManager;
        this.client = client;
    }

    public List<UserProfile> getFriends() {
        try {
            logger.info("Getting friends list...");
            RequestMessage message = new RequestMessage(RequestMessage.REQUEST_GET_FRIENDS, client.getUserId());
            connectionManager.sendMessage(message);
            Message response = connectionManager.readResponse();
            logger.info("Received response from server: {}", response);
            if (response instanceof FriendsListMessage) {
                FriendsListMessage friendsListMessage = (FriendsListMessage) response;
                logger.info("Received friends list");
                return friendsListMessage.getFriends();
            } else if (response instanceof NoResultsMessage) {
                NoResultsMessage noResultsMessage = (NoResultsMessage) response;
                logger.info(noResultsMessage.getMsg());
                return null;
            } else {
                logger.error("Invalid response type");
                return null;
            }
        } catch (Exception e) {
            logger.error("Error getting friends: {}", e.getMessage());
            return null;
        }
    }

    public boolean removeFriend(int userId) {
        try {
            logger.info("Removing friend: {}", userId);
            RequestMessage message = new RequestMessage(RequestMessage.REQUEST_REMOVE_FRIEND, client.getUserId(), userId);
            connectionManager.sendMessage(message);
            Message response = connectionManager.readResponse();
            logger.info("Received response from server: {}", response);
            if (response instanceof FriendSuccessMessage) {
                FriendSuccessMessage friendSuccessMessage = (FriendSuccessMessage) response;
                return friendSuccessMessage.isSuccess();
            } else {
                logger.error("Invalid response type");
                return false;
            }
        } catch (Exception e) {
            logger.error("Error removing friend: {}", e.getMessage());
            return false;
        }
    }

    public boolean friendsWith(int userId) throws IOException {
        /*
        if (!client.isLoggedIn()) {
            throw new IOException("Not logged in");
        }
        */
        logger.info("Checking if user is friends with client...");
        RequestMessage message = new RequestMessage(RequestMessage.REQUEST_FRIENDS_WITH, client.getUserId(), userId);
        connectionManager.sendMessage(message);
        Message response = connectionManager.readResponse();
        if (response instanceof FriendSuccessMessage) {
            FriendSuccessMessage friendsMessage = (FriendSuccessMessage) response;
            logger.info("User is friends with client: {}", friendsMessage.isSuccess());
            return friendsMessage.isSuccess();
        } else {
            throw new IOException("Invalid response type");
        }
    }
}
