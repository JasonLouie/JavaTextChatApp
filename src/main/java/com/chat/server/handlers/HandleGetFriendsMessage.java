package com.chat.server.handlers;

import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chat.database.DatabaseAccessor;
import com.chat.messages.FriendsListMessage;
import com.chat.messages.NoResultsMessage;
import com.chat.messages.RequestMessage;
import com.chat.messages.Message;
import com.chat.models.UserProfile;

/**
 * Handles get friends list requests.
 */
public class HandleGetFriendsMessage extends AbstractMessageHandler {
    private static final Logger logger = LoggerFactory.getLogger(HandleGetFriendsMessage.class);

    public HandleGetFriendsMessage(DatabaseAccessor databaseAccessor) {
        super(databaseAccessor);
    }

    @Override
    public void handleMessage(Message message, DataOutputStream output) throws IOException, SQLException {
        RequestMessage requestMessage = (RequestMessage) message;
        int userId = requestMessage.getIntParam();
        List<UserProfile> friends = databaseAccessor.getFriends(userId);
        if (!friends.isEmpty()) {
            logger.info("Successfully retrieved friends list for user {}", userId);
            new FriendsListMessage(friends).writeTo(output);
        } else {
            logger.info("No friends found for user {}", userId);
            new NoResultsMessage("No friends found").writeTo(output);
        }
    }
}
