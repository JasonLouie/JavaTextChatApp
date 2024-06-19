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
 * Handles accept friend request messages.
 */
public class HandleAcceptFriendRequestMessage extends AbstractMessageHandler {
    private static final Logger logger = LoggerFactory.getLogger(HandleAcceptFriendRequestMessage.class);

    public HandleAcceptFriendRequestMessage(DatabaseAccessor databaseAccessor) {
        super(databaseAccessor);
    }

    @Override
    public void handleMessage(Message message, DataOutputStream output) throws IOException, SQLException {
        RequestMessage requestMessage = (RequestMessage) message;
        int senderId = requestMessage.getIntParam();
        int receiverId = requestMessage.getSecondIntParam();
        logger.info("Handling accept friend request for {} and {}...", senderId, receiverId);
        if (databaseAccessor.checkFriendRequestExists(receiverId, senderId)) {
            databaseAccessor.acceptFriendRequest(receiverId, senderId);
            logger.info("Successfully accepted friend request between {} and {}", receiverId, senderId);
            new FriendSuccessMessage(true).writeTo(output);
        } else {
            logger.info("Friend request does not exist between {} and {}", receiverId, senderId);
            new FriendSuccessMessage(false).writeTo(output);
        }
    }
}
