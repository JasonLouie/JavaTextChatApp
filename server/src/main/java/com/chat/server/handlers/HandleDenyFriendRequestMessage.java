package com.chat.server.handlers;

import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chat.messages.FriendSuccessMessage;
import com.chat.messages.RequestMessage;
import com.server.database.DatabaseAccessor;
import com.chat.messages.Message;

/**
 * Handles deny friend request messages.
 */
public class HandleDenyFriendRequestMessage extends AbstractMessageHandler {
    private static final Logger logger = LoggerFactory.getLogger(HandleDenyFriendRequestMessage.class);

    public HandleDenyFriendRequestMessage(DatabaseAccessor databaseAccessor) {
        super(databaseAccessor);
    }

    @Override
    public void handleMessage(Message message, DataOutputStream output) throws IOException, SQLException {
        RequestMessage requestMessage = (RequestMessage) message;
        int senderId = requestMessage.getIntParam();
        int receiverId = requestMessage.getSecondIntParam();
        if (databaseAccessor.checkFriendRequestExists(receiverId, senderId)) {
            databaseAccessor.denyFriendRequest(receiverId, senderId);
            logger.info("Successfully denied friend request from {} to {}", senderId, receiverId);
            new FriendSuccessMessage(true).writeTo(output);
        } else {
            logger.info("Failed to deny friend request from {} to {}", senderId, receiverId);
            new FriendSuccessMessage(false).writeTo(output);
        }
    }
}
