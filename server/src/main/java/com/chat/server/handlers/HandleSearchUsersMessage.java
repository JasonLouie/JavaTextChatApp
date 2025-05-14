package com.chat.server.handlers;

import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chat.messages.SearchUsersMessage;
import com.chat.messages.NoResultsMessage;
import com.chat.messages.RequestMessage;
import com.chat.messages.Message;
import com.chat.models.UserProfile;
import com.server.database.DatabaseAccessor;

/**
 * Handles search users requests.
 */
public class HandleSearchUsersMessage extends AbstractMessageHandler {
    private static final Logger logger = LoggerFactory.getLogger(HandleSearchUsersMessage.class);
    public HandleSearchUsersMessage(DatabaseAccessor databaseAccessor) {
        super(databaseAccessor);
    }

    @Override
    public void handleMessage(Message message, DataOutputStream output) throws IOException, SQLException {
        RequestMessage requestMessage = (RequestMessage) message;
        String query = requestMessage.getStringParam();
        List<UserProfile> results = databaseAccessor.searchUsers(query);
        if (!results.isEmpty()) {
            logger.info("Found {} users with the username or nickname {}", results.size(), query);
            new SearchUsersMessage(results).writeTo(output);
        } else {
            logger.info("No user with username or nickname {} found", query);
            new NoResultsMessage("No user with username or nickname " + query).writeTo(output);
        }
    }
}
