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
import com.chat.messages.ConversationMessage;
import com.chat.messages.Message;
import com.chat.models.Content;

/**
 * Handles get conversation requests.
 */
public class HandleGetConversationMessage extends AbstractMessageHandler {
    private static final Logger logger = LoggerFactory.getLogger(HandleGetConversationMessage.class);

    public HandleGetConversationMessage(DatabaseAccessor databaseAccessor) {
        super(databaseAccessor);
    }

    @Override
    public void handleMessage(Message message, DataOutputStream output) throws IOException, SQLException {
        RequestMessage requestMessage = (RequestMessage) message;
        int senderId = requestMessage.getIntParam();
        int receiverId = requestMessage.getSecondIntParam();
        if (databaseAccessor.checkConversationExists(senderId, receiverId)) {
            List<Content> conversation = databaseAccessor.getConversation(receiverId);
            logger.info("Retrieved conversation between users {} and {}", senderId, receiverId);
            new ConversationMessage(conversation).writeTo(output);
        } else {
            logger.info("User {} does not have any conversation with {}", senderId, receiverId);
            new NoResultsMessage("User " + senderId + " does not have any conversation with " + receiverId).writeTo(output);
        }
    }
}