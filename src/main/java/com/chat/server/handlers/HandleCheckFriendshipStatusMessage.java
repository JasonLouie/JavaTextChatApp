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
 * Handles check friendship status messages.
 */
public class HandleCheckFriendshipStatusMessage extends AbstractMessageHandler {
    private static final Logger logger = LoggerFactory.getLogger(HandleCheckFriendshipStatusMessage.class);

    public HandleCheckFriendshipStatusMessage(DatabaseAccessor databaseAccessor) {
        super(databaseAccessor);
    }

    @Override
    public void handleMessage(Message message, DataOutputStream output) throws IOException, SQLException {
        RequestMessage requestMessage = (RequestMessage) message;
        int userId = requestMessage.getIntParam();
        int friendId = requestMessage.getSecondIntParam();
        if (databaseAccessor.areFriends(userId, friendId)) {
            logger.info("User {} is friends with {}", userId, friendId);
            new FriendSuccessMessage(true).writeTo(output);
        } else {
            logger.info("User {} is not friends with {}", userId, friendId);
            new FriendSuccessMessage(false).writeTo(output);
        }
    }
}
