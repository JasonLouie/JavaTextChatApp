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
 * Handles check friend request messages.
 */
public class HandleCheckFriendRequestMessage extends AbstractMessageHandler {
    private static final Logger logger = LoggerFactory.getLogger(HandleCheckFriendRequestMessage.class);

    public HandleCheckFriendRequestMessage(DatabaseAccessor databaseAccessor) {
        super(databaseAccessor);
    }

    @Override
    public void handleMessage(Message message, DataOutputStream output) throws IOException, SQLException {
        RequestMessage requestMessage = (RequestMessage) message;
        int senderId = requestMessage.getIntParam();
        int receiverId = requestMessage.getSecondIntParam();
        logger.info("Checking if user {} has a friend request with {}", senderId, receiverId);
        if (databaseAccessor.hasFriendRequest(senderId, receiverId)) {
            logger.info("User {} has a friend request with {}", senderId, receiverId);
            new FriendSuccessMessage(true).writeTo(output);
        } else {
            logger.info("User {} does not have a friend request with {}", senderId, receiverId);
            new FriendSuccessMessage(false).writeTo(output);
        }
    }
}
