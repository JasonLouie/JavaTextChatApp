package com.chat.server.handlers;

import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chat.database.DatabaseAccessor;
import com.chat.messages.FriendSuccessMessage;
import com.chat.messages.RequestMessage;
import com.chat.messages.Message;

/**
 * Handles cancel friend request messages.
 */
public class HandleCancelFriendRequestMessage extends AbstractMessageHandler {
    private static final Logger logger = LoggerFactory.getLogger(HandleCancelFriendRequestMessage.class);

    public HandleCancelFriendRequestMessage(DatabaseAccessor databaseAccessor) {
        super(databaseAccessor);
    }

    @Override
    public void handleMessage(Message message, DataOutputStream output) throws IOException, SQLException {
        RequestMessage requestMessage = (RequestMessage) message;
        int senderId = requestMessage.getIntParam();
        int receiverId = requestMessage.getSecondIntParam();
        if (databaseAccessor.checkFriendRequestExists(senderId, receiverId)) {
            databaseAccessor.denyFriendRequest(senderId, receiverId);
            logger.info("Successfully cancelled friend request from {} to {}", senderId, receiverId);
            new FriendSuccessMessage(true).writeTo(output);
        } else {
            logger.info("Failed to cancel friend request from {} to {}", senderId, receiverId);
            new FriendSuccessMessage(false).writeTo(output);
        }
    }
}
