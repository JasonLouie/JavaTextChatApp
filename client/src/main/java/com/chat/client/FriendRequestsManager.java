package com.chat.client;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chat.messages.FriendRequestMessage;
import com.chat.messages.FriendSuccessMessage;
import com.chat.messages.Message;
import com.chat.messages.NoResultsMessage;
import com.chat.messages.RequestMessage;
import com.chat.models.UserProfile;

public class FriendRequestsManager {
    private static final Logger logger = LoggerFactory.getLogger(FriendRequestsManager.class);
    private ConnectionManager connectionManager;
    private Client client;

    public FriendRequestsManager(ConnectionManager connectionManager, Client client) {
        this.connectionManager = connectionManager;
        this.client = client;
    }

    public List<UserProfile> getFriendRequests() {
        try {
            logger.info("Getting friend requests...");
            RequestMessage message = new RequestMessage(RequestMessage.REQUEST_GET_FRIEND_REQUESTS, client.getUserId());
            connectionManager.sendMessage(message);
            Message response = connectionManager.readResponse();
            logger.info("Received response from server");
            if (response instanceof FriendRequestMessage) {
                FriendRequestMessage friendRequestMessage = (FriendRequestMessage) response;
                return friendRequestMessage.getFriendRequests();
            } else if (response instanceof NoResultsMessage) {
                NoResultsMessage noResultsMessage = (NoResultsMessage) response;
                logger.info(noResultsMessage.getMsg());
                return null;
            } else {
                logger.error("Invalid response type");
                return null;
            }
        } catch (Exception e) {
            logger.error("Error getting friend requests: {}", e.getMessage());
            return null;
        }
    }

    public boolean sendFriendRequest(int userId) {
        try {
            logger.info("Sending friend request to user: {}", userId);
            RequestMessage message = new RequestMessage(RequestMessage.REQUEST_SEND_FRIEND_REQUEST, client.getUserId(), userId);
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
            logger.error("Error sending friend request: {}", e.getMessage());
            return false;
        }
    }

    public boolean cancelFriendRequest(int userId) {
        try {
            logger.info("Cancelling friend request to user: {}", userId);
            RequestMessage message = new RequestMessage(RequestMessage.REQUEST_CANCEL_FRIEND_REQUEST, client.getUserId(), userId);
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
            logger.error("Error cancelling friend request: {}", e.getMessage());
            return false;
        }
    }

    public boolean acceptFriendRequest(int userId) {
        try {
            logger.info("Accepting friend request from user: {}", userId);
            RequestMessage message = new RequestMessage(RequestMessage.REQUEST_ACCEPT_FRIEND_REQUEST, client.getUserId(), userId);
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
            logger.error("Error accepting friend request: {}", e.getMessage());
            return false;
        }
    }

    public boolean denyFriendRequest(int userId) {
        try {
            logger.info("Denying friend request from user: {}", userId);
            RequestMessage message = new RequestMessage(RequestMessage.REQUEST_DENY_FRIEND_REQUEST, client.getUserId(), userId);
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
            logger.error("Error denying friend request: {}", e.getMessage());
            return false;
        }
    }

    public boolean hasFriendRequest(int userId) {
        /*
        if (!client.isLoggedIn()) {
            throw new IOException("Not logged in");
        }
        */
        logger.info("Checking if user {} has friend request from client...", userId);
        try {
            RequestMessage message = new RequestMessage(RequestMessage.REQUEST_HAS_FRIEND_REQUEST, client.getUserId(), userId);
            connectionManager.sendMessage(message);
            Message response = connectionManager.readResponse();
            if (response instanceof FriendSuccessMessage) {
                FriendSuccessMessage friendsMessage = (FriendSuccessMessage) response;
                logger.info("User has a friend request with client: {}", friendsMessage.isSuccess());
                return friendsMessage.isSuccess();
            } else {
                logger.error("Invalid response type");
                return false;
            }
        } catch (Exception e) {
            logger.error("Error checking for friend request: {}", e.getMessage());
            return false;
        }
    }
}
