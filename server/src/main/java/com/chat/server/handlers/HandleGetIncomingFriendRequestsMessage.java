package com.chat.server.handlers;

import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chat.messages.FriendRequestMessage;
import com.chat.messages.NoResultsMessage;
import com.chat.messages.RequestMessage;
import com.chat.messages.Message;
import com.chat.models.UserProfile;
import com.server.database.DatabaseAccessor;

/**
 * Handles get incoming friend requests.
 */
public class HandleGetIncomingFriendRequestsMessage extends AbstractMessageHandler {
    private static final Logger logger = LoggerFactory.getLogger(HandleGetIncomingFriendRequestsMessage.class);
    public HandleGetIncomingFriendRequestsMessage(DatabaseAccessor databaseAccessor) {
        super(databaseAccessor);
    }

    @Override
    public void handleMessage(Message message, DataOutputStream output) throws IOException, SQLException {
        RequestMessage requestMessage = (RequestMessage) message;
        int userId = requestMessage.getIntParam();
        List<UserProfile> friendRequests = databaseAccessor.getFriendRequests(userId);
        if (!friendRequests.isEmpty()) {
            logger.info("Successfully retrieved friend requests for user {}", userId);
            new FriendRequestMessage(friendRequests).writeTo(output);
        } else {
            logger.info("No friend requests found for user {}", userId);
            new NoResultsMessage("No friend requests found").writeTo(output);
        }
    }
}