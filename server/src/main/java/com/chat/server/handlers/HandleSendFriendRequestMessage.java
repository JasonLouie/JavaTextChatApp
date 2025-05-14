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
 * Handles send friend request messages.
 */
public class HandleSendFriendRequestMessage extends AbstractMessageHandler {
    private static final Logger logger = LoggerFactory.getLogger(HandleSendFriendRequestMessage.class);

    public HandleSendFriendRequestMessage(DatabaseAccessor databaseAccessor) {
        super(databaseAccessor);
    }

    @Override
    public void handleMessage(Message message, DataOutputStream output) throws IOException, SQLException {
        RequestMessage requestMessage = (RequestMessage) message;
        int senderId = requestMessage.getIntParam();
        int receiverId = requestMessage.getSecondIntParam();
        logger.info("Handling send friend request for {} and {}...", senderId, receiverId);
        if (databaseAccessor.checkFriendRequestExists(receiverId, senderId)) {
            logger.info("Friend request already exists between {} and {}", senderId, receiverId);
            new FriendSuccessMessage(false).writeTo(output);
        } else {
            databaseAccessor.sendFriendRequest(senderId, receiverId);
            logger.info("Successfully sent friend request from {} to {}", senderId, receiverId);
            new FriendSuccessMessage(true).writeTo(output);
        }
    }
}
