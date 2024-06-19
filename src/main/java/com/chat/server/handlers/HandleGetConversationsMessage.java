package com.chat.server.handlers;

import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chat.messages.RequestMessage;
import com.chat.messages.NoResultsMessage;
import com.chat.database.DatabaseAccessor;
import com.chat.messages.ConversationsMessage;
import com.chat.messages.ErrorMessage;
import com.chat.messages.Message;
import com.chat.models.Conversation;

/**
 * Handles get conversations requests.
 */
public class HandleGetConversationsMessage extends AbstractMessageHandler {
    private static final Logger logger = LoggerFactory.getLogger(HandleGetConversationsMessage.class);

    public HandleGetConversationsMessage(DatabaseAccessor databaseAccessor) {
        super(databaseAccessor);
    }

    @Override
    public void handleMessage(Message message, DataOutputStream output) throws IOException, SQLException {
        RequestMessage requestMessage = (RequestMessage) message;
        int userId = requestMessage.getIntParam();
        if (databaseAccessor.checkUserExists(userId)) {
            List<Conversation> conversations = databaseAccessor.getConversations(userId);
            if (conversations.isEmpty()) {
                logger.info("User does not have any conversations");
                new NoResultsMessage("User does not have any conversations").writeTo(output);
            } else {
                logger.info("Successfully retrieved user's conversations");
                new ConversationsMessage(conversations).writeTo(output);
            }
        } else {
            logger.error("User does not exist");
            new ErrorMessage("User does not exist").writeTo(output);
        }
    }
}