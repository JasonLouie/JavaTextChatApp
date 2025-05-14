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
 * Handles remove friend messages.
 */
public class HandleRemoveFriendMessage extends AbstractMessageHandler {
    private static final Logger logger = LoggerFactory.getLogger(HandleRemoveFriendMessage.class);

    public HandleRemoveFriendMessage(DatabaseAccessor databaseAccessor) {
        super(databaseAccessor);
    }

    @Override
    public void handleMessage(Message message, DataOutputStream output) throws IOException, SQLException {
        RequestMessage requestMessage = (RequestMessage) message;
        int userId = requestMessage.getIntParam();
        int friendId = requestMessage.getSecondIntParam();
        if (databaseAccessor.areFriends(userId, friendId)) {
            databaseAccessor.removeFriend(userId, friendId);
            logger.info("Successfully removed user {} from user {}'s friend list", friendId, userId);
            new FriendSuccessMessage(true).writeTo(output);
        } else {
            logger.info("Could not remove user {} from user {}'s friend list because they are not friends", friendId, userId);
            new FriendSuccessMessage(false).writeTo(output);
        }
    }
}
